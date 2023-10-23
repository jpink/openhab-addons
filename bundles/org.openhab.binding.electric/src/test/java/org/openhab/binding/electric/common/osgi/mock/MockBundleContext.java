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

import static java.util.Collections.singleton;
import static org.openhab.binding.electric.common.Collections.find;
import static org.openhab.binding.electric.common.Collections.first;
import static org.openhab.binding.electric.common.Collections.nullify;
import static org.openhab.binding.electric.common.Reflections.type;
import static org.openhab.binding.electric.common.Reflections.types;
import static org.openhab.binding.electric.common.osgi.mock.MockFramework.cast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.AllServiceListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Bundle context mock.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault({})
public class MockBundleContext extends MockBundleReference implements BundleContext {
    final @Nullable BundleActivator activator;
    private final Set<FrameworkListener> frameworkListeners = new LinkedHashSet<>();
    private final Set<BundleListener> bundleListeners = new LinkedHashSet<>();
    private final Set<ServiceListener> serviceListeners = new LinkedHashSet<>();
    private boolean invalid;

    // private final Map<MockServiceReference<?>, Integer> useCounts = new HashMap<>();
    // private final Map<MockServiceReference<?>, Object> cache = new HashMap<>();

    MockBundleContext(MockBundle bundle, @Nullable BundleActivator activator) {
        super(bundle);
        this.activator = activator;
    }

    @Override
    public @Nullable String getProperty(String key) {
        return framework().getProperty(key);
    }

    @Override
    public MockBundle installBundle(String location, InputStream input) throws BundleException {
        logger.info("Install bundle from location '{}'.", location);
        var manifest = new Manifest();
        try {
            manifest.read(input);
        } catch (IOException e) {
            throw new BundleException("Can't read manifest!", BundleException.READ_ERROR, e);
        }
        var headers = new Hashtable<String, @Nullable String>();
        var attributes = manifest.getMainAttributes();
        attributes.keySet().forEach(key -> {
            var name = key.toString();
            headers.put(name, attributes.getValue(name));
        });
        return framework().createBundle(location, headers);
    }

    @Override
    public Bundle installBundle(String location) throws BundleException {
        throw new BundleException("Installing bundle only by location '" + location + "' isn't supported!",
                BundleException.RESOLVE_ERROR);
    }

    @Override
    public @Nullable Bundle getBundle(long id) {
        logger.info("Get bundle #{}", id);
        return framework().getBundle(id);
    }

    @Override
    public Bundle[] getBundles() {
        logger.info("Get installed bundles");
        return framework().getBundles();
    }

    @Override
    public void addServiceListener(ServiceListener listener, @Nullable String filter) throws InvalidSyntaxException {
        logger.info("Add service listener {} with filter {}.", listener, filter);
        ensureValid();
        if (serviceListeners.add(listener)) {
            framework().add(listener,
                    listener instanceof AllServiceListener || filter == null ? null : createFilter(filter));
        } else {
            logger.warn("Listener already added!");
        }
    }

    @Override
    public void addServiceListener(ServiceListener listener) {
        logger.info("Add service listener {} without filter.", listener);
        ensureValid();
        if (serviceListeners.add(listener)) {
            framework().add(listener, null);
        } else {
            logger.warn("Listener already added!");
        }
    }

    @Override
    public void removeServiceListener(ServiceListener listener) {
        logger.info("Remove service listener {}.", listener);
        ensureValid();
        if (serviceListeners.remove(listener)) {
            framework().remove(listener);
        } else {
            logger.warn("Listener not found!");
        }
    }

    @Override
    public void addBundleListener(BundleListener listener) {
        logger.info("Add bundle listener {}.", listener);
        ensureValid();
        if (bundleListeners.add(listener)) {
            bundle.add(listener);
        } else {
            logger.warn("Listener already added!");
        }
    }

    @Override
    public void removeBundleListener(BundleListener listener) {
        logger.info("Remove bundle listener {}.", listener);
        ensureValid();
        if (bundleListeners.remove(listener)) {
            bundle.remove(listener);
        } else {
            logger.warn("Listener not found!");
        }
    }

    @Override
    public void addFrameworkListener(FrameworkListener listener) {
        logger.warn("Add framework listener {}.", listener);
        ensureValid();
        if (frameworkListeners.remove(listener)) {
            framework().listeners.remove(listener);
        } else {
            logger.warn("Listener already added!");
        }
    }

    @Override
    public void removeFrameworkListener(FrameworkListener listener) {
        logger.warn("Remove framework listener {}.", listener);
        ensureValid();
        if (frameworkListeners.remove(listener)) {
            framework().listeners.remove(listener);
        } else {
            logger.warn("Listener not found!");
        }
    }

    @Override
    public ServiceRegistration<?> registerService(String[] clazzes, Object service,
            @Nullable Dictionary<String, ?> properties) {
        logger.info("Registering {} types of service.", String.join(", ", clazzes));
        ensureValid();
        var locators = types(clazzes);
        if (locators.isEmpty()) {
            throw new IllegalArgumentException("Locator class name required!");
        }
        MockServiceRegistration<?> registration;
        if (service instanceof ServiceFactory<?> factory) {
            registration = MockServiceRegistration.create(bundle, locators, factory, properties);
        } else {
            var serviceType = service.getClass();
            locators.forEach(locator -> {
                if (!locator.isAssignableFrom(serviceType)) {
                    throw new IllegalArgumentException("Service doesn't implement " + locator);
                }
            });
            registration = MockServiceRegistration.create(bundle, locators, service, properties);
        }
        framework().register(registration);
        return registration;
    }

    @Override
    public ServiceRegistration<?> registerService(String locator, Object service,
            @Nullable Dictionary<String, ?> properties) {
        return registerService(new String[] { locator }, service, properties);
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> locator, S service,
            @Nullable Dictionary<String, ?> properties) {
        logger.info("Registering {} type of service.", locator);
        ensureValid();
        return framework().register(MockServiceRegistration.create(bundle, singleton(locator), service, properties));
    }

    @Override
    public <S> ServiceRegistration<S> registerService(Class<S> locator, ServiceFactory<S> factory,
            @Nullable Dictionary<String, ?> properties) {
        logger.info("Registering {} type of service factory.", locator);
        ensureValid();
        return framework().register(MockServiceRegistration.create(bundle, singleton(locator), factory, properties));
    }

    @Override
    public ServiceReference<?> @Nullable [] getServiceReferences(@Nullable String clazz, @Nullable String filter)
            throws InvalidSyntaxException {
        ensureValid();
        return nullify(framework().references(clazz, filter)
                .filter(reference -> reference.isAssignableTo(bundle, clazz)).toArray(ServiceReference[]::new));
    }

    @Override
    public ServiceReference<?> @Nullable [] getAllServiceReferences(@Nullable String clazz, @Nullable String filter)
            throws InvalidSyntaxException {
        ensureValid();
        return nullify(framework().references(clazz, filter).toArray(ServiceReference[]::new));
    }

    @Override
    public @Nullable ServiceReference<?> getServiceReference(String clazz) {
        return getServiceReference(type(clazz));
    }

    @Override
    public <S> @Nullable ServiceReference<S> getServiceReference(Class<S> clazz) {
        ensureValid();
        var references = framework().references(clazz, null).toList();
        return switch (references.size()) {
            case 0 -> null;
            case 1 -> references.get(0);
            default -> {
                var highestRanking = references.stream().mapToInt(MockServiceReference::ranking).max().orElseThrow();
                references = references.stream().filter(reference -> reference.ranking() == highestRanking).toList();
                if (references.size() == 1) {
                    yield references.get(0);
                }
                var lowestId = references.stream().mapToLong(MockServiceReference::id).min().orElseThrow();
                yield first(references, reference -> reference.id() == lowestId);
            }
        };
    }

    @Override
    public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter)
            throws InvalidSyntaxException {
        ensureValid();
        var className = clazz.getName();
        return framework().references(clazz, createFilter(filter))
                .filter(reference -> reference.isAssignableTo(bundle, className)).collect(Collectors.toList());
    }

    @Override
    @NonNullByDefault({})
    public <S> S getService(ServiceReference<S> reference) {
        logger.info("Get service {}.", reference);
        return cast(reference).getService(this);
    }

    @Override
    public boolean ungetService(ServiceReference<?> reference) {
        logger.info("Release service {}.", reference);
        return cast(reference).releaseService(this);
    }

    @Override
    public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
        logger.info("Get service objects {}.", reference);
        return cast(reference).getServiceObjects(this);
    }

    @Override
    public @Nullable File getDataFile(String filename) {
        logger.info("Get data file {}", filename);
        ensureValid();
        return new File(filename);
    }

    @Override
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        logger.info("Create filter {}", filter);
        return FrameworkUtil.createFilter(filter);
    }

    @Override
    public @Nullable Bundle getBundle(String location) {
        logger.info("Get bundle by location {}", location);
        return find(framework().bundles.values(), bundle -> bundle.location.equals(location));
    }

    void ensureValid() {
        if (invalid) {
            throw new IllegalStateException(this + " is no longer valid!");
        }
    }

    /** Things to do after being invalid. */
    void dispose() {
        invalid = true;
        // TODO
    }

    @Override
    public String toString() {
        return super.toString() + " context";
    }
}
