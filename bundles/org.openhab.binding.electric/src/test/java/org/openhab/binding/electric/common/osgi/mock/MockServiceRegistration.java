/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.electric.common.osgi.mock;

import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.framework.Constants.SERVICE_BUNDLEID;
import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.framework.Constants.SERVICE_SCOPE;

import java.util.Dictionary;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * Service registration mocks.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault({})
public abstract class MockServiceRegistration<S> extends MockBundleReference
        implements ServiceRegistration<S>, Comparable<MockServiceRegistration<S>> {
    private static class AsSingleton<S> extends MockServiceRegistration<S> {
        private final S service;

        protected AsSingleton(MockBundle registrant, Set<Class<?>> locators, S service,
                @Nullable Dictionary<String, ?> properties) {
            super(registrant, locators, Constants.SCOPE_SINGLETON, properties);
            this.service = service;
        }

        @Override
        S getService(MockServiceObjects<S> serviceObjects) {
            return service;
        }

        @Override
        void releaseService(Bundle bundle, S service) {
        }

        @Override
        public String toString() {
            return "Singleton" + super.toString();
        }
    }

    private static class AsBundle<S> extends MockServiceRegistration<S> {
        private final ServiceFactory<S> factory;

        protected AsBundle(MockBundle registrant, Set<Class<?>> locators, ServiceFactory<S> factory,
                @Nullable Dictionary<String, ?> properties) {
            super(registrant, locators, Constants.SCOPE_BUNDLE, properties);
            this.factory = factory;
        }

        @Override
        S getService(MockServiceObjects<S> serviceObjects) {
            if (serviceObjects.using()) {
                return serviceObjects.cached();
            } else {
                var requestingBundle = serviceObjects.bundle;
                requestingBundle.servicesInUse.add(reference);
                return getService(factory, requestingBundle);
            }
        }

        @Override
        void releaseService(Bundle bundle, S service) {
            factory.ungetService(bundle, this, service);
        }

        @Override
        public String toString() {
            return "Bundle" + super.toString();
        }
    }

    private static class AsPrototype<S> extends MockServiceRegistration<S> {
        private final PrototypeServiceFactory<S> factory;

        protected AsPrototype(MockBundle registrant, Set<Class<?>> locators, PrototypeServiceFactory<S> factory,
                @Nullable Dictionary<String, ?> properties) {
            super(registrant, locators, Constants.SCOPE_PROTOTYPE, properties);
            this.factory = factory;
        }

        @Override
        S getService(MockServiceObjects<S> serviceObjects) {
            var requestingBundle = serviceObjects.bundle;
            requestingBundle.servicesInUse.add(reference);
            return getService(factory, requestingBundle);
        }

        @Override
        void releaseService(Bundle bundle, S service) {
            factory.ungetService(bundle, this, service);
        }

        @Override
        public String toString() {
            return "Prototype" + super.toString();
        }
    }

    static <S> MockServiceRegistration<S> create(MockBundle registrant, Set<Class<?>> locators,
            ServiceFactory<S> factory, @Nullable Dictionary<String, ?> properties) {
        return factory instanceof PrototypeServiceFactory<S> prototypeFactory
                ? new AsPrototype<>(registrant, locators, prototypeFactory, properties)
                : new AsBundle<>(registrant, locators, factory, properties);
    }

    static <S> MockServiceRegistration<S> create(MockBundle registrant, Set<Class<?>> locators, S service,
            @Nullable Dictionary<String, ?> properties) {
        return new AsSingleton<>(registrant, locators, service, properties);
    }

    final long id;
    final Set<Class<?>> locators;
    Map<String, ?> properties;
    final MockServiceReference<S> reference = new MockServiceReference<>(this);
    boolean unregistered;

    protected MockServiceRegistration(MockBundle registrant, Set<Class<?>> locators, String scope,
            @Nullable Dictionary<String, ?> properties) {
        super(registrant);
        id = ++framework().serviceIdSequence;
        this.locators = locators;
        this.properties = buildProperties(properties, scope);
    }

    private Map<String, Object> buildProperties(@Nullable Dictionary<String, ?> properties, String scope) {
        var map = new TreeMap<String, Object>();
        if (properties != null) {
            map.putAll(FrameworkUtil.asMap(properties));
        }
        map.put(SERVICE_ID, id);
        map.put(OBJECTCLASS, locators.stream().map(Class::getName).toArray(String[]::new));
        map.put(SERVICE_SCOPE, scope);
        map.put(SERVICE_BUNDLEID, bundleId());
        return map;
    }

    @Override
    public MockServiceReference<S> getReference() {
        if (unregistered) {
            throw new IllegalStateException("Service has already been unregistered!");
        }
        return reference;
    }

    @Override
    public void setProperties(Dictionary<String, ?> properties) {
        logger.info("Set properties");
        this.properties = buildProperties(properties, (String) properties.get(SERVICE_SCOPE));
        // TODO
    }

    @Override
    public void unregister() {
        logger.info("Unregister");
        unregistered = true;
        framework().unregister(this);
    }

    @Override
    public int compareTo(MockServiceRegistration<S> o) {
        return (int) id - (int) o.id;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof MockServiceRegistration<?> that && id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return " scoped service #" + id + " of " + String.join(", ", locators.stream().map(Class::toString).toList())
                + " registered by " + super.toString();
    }

    protected S getService(ServiceFactory<S> factory, Bundle bundle) {
        try {
            var service = factory.getService(bundle, this);
            if (service == null) {
                framework().fire(new FrameworkEvent(FrameworkEvent.ERROR, bundle,
                        new ServiceException("Null service!", ServiceException.FACTORY_ERROR)));
                return null;
            }
            var serviceType = service.getClass();
            for (var locator : locators) {
                if (!locator.isAssignableFrom(serviceType)) {
                    framework().fire(new FrameworkEvent(FrameworkEvent.ERROR, bundle, new ServiceException(
                            "Service doesn't implement " + locator + "!", ServiceException.FACTORY_ERROR)));
                    return null;
                }
            }
            return service;
        } catch (Exception e) {
            framework().fire(new FrameworkEvent(FrameworkEvent.ERROR, bundle,
                    new ServiceException("Service factory throws exception!", ServiceException.FACTORY_EXCEPTION)));
            return null;
        }
    }

    abstract S getService(MockServiceObjects<S> serviceObjects);

    abstract void releaseService(Bundle bundle, S service);
}
