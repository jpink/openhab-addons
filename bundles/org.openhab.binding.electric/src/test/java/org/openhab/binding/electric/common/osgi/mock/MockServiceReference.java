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

import static org.openhab.binding.electric.common.Text.decapitalize;
import static org.openhab.binding.electric.common.osgi.mock.MockFramework.cast;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;

/**
 * Service reference mock.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault({})
public class MockServiceReference<S> extends MockBundleReference implements ServiceReference<S> {
    final MockServiceRegistration<S> registration;
    private final ThreadLocal<@Nullable Bundle> requester = new ThreadLocal<>();
    final Map<MockBundle, MockServiceObjects<S>> objectsByBundle = new HashMap<>();

    public MockServiceReference(MockServiceRegistration<S> registration) {
        super(registration);
        this.registration = registration;
    }

    @Override
    public @Nullable Object getProperty(String key) {
        var value = properties().get(key);
        logger.info("Got property {} of value {}.", key, value);
        return value;
    }

    @Override
    public String[] getPropertyKeys() {
        var keys = properties().keySet().toArray(String[]::new);
        logger.info("Got property keys {}.", String.join(", ", keys));
        return keys;
    }

    @Override
    public @Nullable Bundle getBundle() {
        logger.info("Get bundle");
        return registration.unregistered ? null : bundle;
    }

    @Override
    public Bundle[] getUsingBundles() {
        logger.info("Get using bundles.");
        return objectsByBundle.values().stream().filter(MockServiceObjects::using).map(objects -> objects.bundle)
                .toArray(Bundle[]::new);
    }

    @Override
    public boolean isAssignableTo(Bundle bundle, @Nullable String className) {
        logger.info("Is assignable to {} bundle and {} class name.", bundle, className);
        // 1. If the specified bundle is equal to the bundle that registered the service referenced by this
        // ServiceReference (registrant bundle) return true.
        // If class name is missing we can't check it. And in unit tests the sources are always the same.
        return this.bundle.equals(cast(bundle)) || className != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Object reference) {
        return registration.compareTo(cast((ServiceReference<S>) reference).registration);
    }

    @Override
    public Dictionary<String, Object> getProperties() {
        logger.info("Get properties");
        return FrameworkUtil.asDictionary(properties());
    }

    @Override
    public <A> A adapt(Class<A> type) {
        logger.info("Adapt to {}", type);
        return type.cast(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof MockServiceReference<?> that && registration.equals(that.registration);
    }

    @Override
    public int hashCode() {
        return registration.hashCode();
    }

    @Override
    public String toString() {
        return "Reference to " + decapitalize(registration.toString());
    }

    @Nullable
    S getService(MockBundleContext bundleContext) {
        try {
            var first = requester.get();
            var bundle = bundleContext.bundle;
            if (first != null) {
                framework().fire(new FrameworkEvent(FrameworkEvent.ERROR, bundle,
                        new ServiceException(first + " is getting first " + this + " and then " + bundle + "!",
                                ServiceException.FACTORY_RECURSION)));
                return null;
            }
            requester.set(bundle);
            return getServiceObjects(bundleContext).getService();
        } finally {
            requester.remove();
        }
    }

    public boolean releaseService(MockBundleContext bundleContext) {
        return getServiceObjects(bundleContext).releaseService();
    }

    MockServiceObjects<S> getServiceObjects(MockBundleContext bundleContext) {
        return objectsByBundle.computeIfAbsent(bundleContext.bundle,
                b -> new MockServiceObjects<>(this, bundleContext));
    }

    long id() {
        return registration.id;
    }

    private Map<String, ?> properties() {
        return registration.properties;
    }

    int ranking() {
        return (getProperty(Constants.SERVICE_RANKING) instanceof Integer ranking) ? ranking : 0;
    }
}
