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

import static org.openhab.binding.electric.common.Collections.first;
import static org.openhab.binding.electric.common.Reflections.type;
import static org.osgi.framework.FrameworkUtil.createFilter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;

/**
 * Framework mock.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault({})
public class MockFramework extends MockBundle implements Framework {
    record FilteredServiceListener(ServiceListener listener, @Nullable Filter filter) {
    }

    static class ActivatorError extends BundleException {
        ActivatorError(String name, String message, Exception cause) {
            super("The bundle activator class " + name + " " + message + "!", ACTIVATOR_ERROR, cause);
        }

        ActivatorError(BundleActivator activator, String message, Exception cause) {
            this(activator.getClass().getName(), message, cause);
        }
    }

    private static class OtherFramework extends IllegalArgumentException {
        private OtherFramework(Object object) {
            super("The specified " + object + " was not created by mock framework!");
        }
    }

    private static class MockFrameworkStartLevel extends MockBundleReference implements FrameworkStartLevel {
        private int active;
        private int initial = 1;

        private MockFrameworkStartLevel(MockFramework framework) {
            super(framework);
        }

        @Override
        public int getStartLevel() {
            logger.info("Get active start level {}", active);
            return active;
        }

        @Override
        public void setStartLevel(int startLevel, FrameworkListener... listeners) {
            logger.info("Modify active start level from {} to {}. Notify {} listeners.", active, startLevel,
                    listeners.length);
            EXECUTOR.execute(() -> changeStartLevel(startLevel, listeners));
        }

        @Override
        public int getInitialBundleStartLevel() {
            logger.info("Get initial start level {}", initial);
            return initial;
        }

        @Override
        public void setInitialBundleStartLevel(int startLevel) {
            logger.info("Set initial start level to {}. Previous was {}.", startLevel, initial);
            if (startLevel < 1) {
                throw new IllegalArgumentException("Start level must be positive!");
            }
            initial = startLevel;
        }

        private void changeStartLevel(int startLevel, FrameworkListener... listeners) {
            // TODO
            active = startLevel;
            var event = new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, bundle, null);
            Arrays.stream(listeners).forEach(listener -> listener.frameworkEvent(event));
        }
    }

    static final ExecutorService EXECUTOR = Executors.newWorkStealingPool();
    private static int FRAMEWORK_SEQUENCE;

    static MockBundle cast(Bundle bundle) {
        if (bundle instanceof MockBundle mock) {
            return mock;
        } else {
            throw new OtherFramework(bundle);
        }
    }

    static <S> MockServiceReference<S> cast(ServiceReference<S> reference) {
        if (reference instanceof MockServiceReference<S> mock) {
            return mock;
        } else {
            throw new OtherFramework(reference);
        }
    }

    protected long bundleSequence;
    private final Properties properties = new Properties(System.getProperties()) {
        {
            put(Constants.FRAMEWORK_VERSION, "1.0.0");
            put(Constants.FRAMEWORK_VENDOR, "openHAB");
            put(Constants.FRAMEWORK_LANGUAGE, System.getProperty("user.language"));
            put(Constants.FRAMEWORK_OS_NAME, System.getProperty("os.name"));
            put(Constants.FRAMEWORK_OS_VERSION, System.getProperty("os.version"));
            put(Constants.FRAMEWORK_PROCESSOR, System.getProperty("os.arch"));
        }
    };
    private final FrameworkStartLevel level = new MockFrameworkStartLevel(this);
    final Set<FrameworkListener> listeners = new LinkedHashSet<>();
    private final Set<MockServiceRegistration<?>> serviceRegistry = new LinkedHashSet<>();
    long serviceIdSequence;
    private final Set<FilteredServiceListener> serviceListeners = new LinkedHashSet<>();
    private boolean initialized;
    final Map<Long, MockBundle> bundles = new TreeMap<>();

    public MockFramework() {
        super(null, Constants.SYSTEM_BUNDLE_LOCATION, new Hashtable<>(), 0);
        headers.put(Constants.BUNDLE_NAME, "#" + ++FRAMEWORK_SEQUENCE + " mock framework");
        headers.put(Constants.BUNDLE_SYMBOLICNAME, Constants.SYSTEM_BUNDLE_SYMBOLICNAME);
        install(this);
    }

    // #region MockBundle overrides
    @Override
    public void start() {
        logger.info("Start");
        if (isStarting())
            return;
        active();
        fireFrameworkEvent(FrameworkEvent.STARTED);
    }

    @Override
    public void start(int options) {
        start();
    }

    @Override
    public void stop() {
        logger.info("Stop");
        stopping();
        // registeredServices.forEach(service -> service.); TODO Unregister services
        resolved();
    }

    @Override
    public void stop(int options) {
        stop();
    }

    @Override
    public void uninstall() throws BundleException {
        throw new BundleException("Framework can't be uninstalled!");
    }

    @Override
    public void update() throws BundleException {
        logger.info("Update");
        stop();
        start();
    }

    @Override
    public void update(@Nullable InputStream input) throws BundleException {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        update();
    }

    @Override
    public @Nullable Enumeration<String> getEntryPaths(String path) {
        logger.info("Get entry paths '{}'.", path);
        notUninstalled();
        return null;
    }

    @Override
    public @Nullable URL getEntry(String name) {
        logger.info("Get entry '{}'.", name);
        notUninstalled();
        return null;
    }

    @Override
    public @Nullable Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
        logger.info("Find entries from {} of {}.", path, filePattern);
        notUninstalled();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> A adapt(Class<A> type) {
        logger.info("Adapt to {}", type);
        if (!initialized) {
            return null;
        }
        if (FrameworkStartLevel.class.equals(type)) {
            return (A) level;
        }
        if (BundleStartLevel.class.equals(type)) {
            return super.adapt(type);
        }
        try {
            return type.cast(this);
        } catch (ClassCastException e) {
            return null;
        }
    }
    // #endregion

    // #region Framework implementation
    @Override
    public void init() throws BundleException {
        logger.info("Init");
        if (isStartingActiveOrStopping())
            return;
        properties.setProperty(Constants.FRAMEWORK_UUID, UUID.randomUUID().toString());
        starting();
        context = createContext(this);
        // TODO Enable event handling
        // TODO Install all bundles
        // TODO Register framework services
        initialized = true;
        start();
    }

    @Override
    public void init(FrameworkListener... listeners) throws BundleException {
        logger.info("Add {} framework listeners.", listeners.length);
        this.listeners.addAll(Arrays.stream(listeners).toList());
        init();
    }

    @Override
    public FrameworkEvent waitForStop(long timeout) throws InterruptedException {
        logger.info("Wait for stop {} ms.", timeout);
        // TODO
        return new FrameworkEvent(FrameworkEvent.STOPPED, this, null);
    }
    // #endregion

    void add(ServiceListener listener, @Nullable Filter filter) {
        logger.info("Add service listener {} with filter {}", listener, filter);
        serviceListeners.add(new FilteredServiceListener(listener, filter));
    }

    private void fireFrameworkEvent(int type) {
        logger.info("Fire framework event #{}", type);
        fire(new FrameworkEvent(type, this, null));
    }

    void fire(FrameworkEvent event) {
        EXECUTOR.execute(() -> {
            logger.info("Fire framework event {}", event);
            listeners.forEach(listener -> listener.frameworkEvent(event));
        });
    }

    void remove(ServiceListener listener) {
        logger.info("Remove service listener {}.", listener);
        serviceListeners.remove(first(serviceListeners, filtered -> filtered.listener.equals(listener)));
    }

    private MockBundle install(MockBundle bundle) {
        var id = bundle.getBundleId();
        logger.info("Installed #{} from {} with manifest:", id, bundle.location);
        headers.forEach((key, value) -> logger.info("{}: {}", key, value));
        bundles.put(id, bundle);
        return bundle;
    }

    public void resolve(MockBundle bundle) throws BundleException {
        logger.info("Resolve {} bundle.", bundle);
        if (bundle.isInstalled()) {
            bundle.resolved();
        } else {
            throw new BundleException("The bundle isn't installed!", BundleException.RESOLVE_ERROR);
        }
    }

    public @Nullable BundleActivator createActivator(Bundle bundle) throws BundleException {
        logger.info("Create activator for {} bundle.", bundle);
        var name = bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);
        if (name == null) {
            logger.info("Bundle doesn't have activator.");
            return null;
        } else {
            try {
                logger.info("Create bundle activator of class {}.", name);
                return (BundleActivator) Class.forName(name).getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                throw new ActivatorError(name, "not found", e);
            } catch (NoSuchMethodException e) {
                throw new ActivatorError(name, "doesn't have parameterless constructor", e);
            } catch (InvocationTargetException e) {
                throw new ActivatorError(name, "throws exception when instantiating!", e);
            } catch (InstantiationException e) {
                throw new ActivatorError(name, "isn't instantiable!", e);
            } catch (IllegalAccessException e) {
                throw new ActivatorError(name, "doesn't have public constructor!", e);
            } catch (ClassCastException e) {
                throw new ActivatorError(name, "doesn't implement `BundleActivator`!", e);
            }
        }
    }

    MockBundle createBundle(String location, Hashtable<String, @Nullable String> headers) {
        return install(new MockBundle(this, location, headers, level.getInitialBundleStartLevel()));
    }

    public MockBundleContext createContext(MockBundle bundle) throws BundleException {
        logger.info("Create bundle context for {} bundle.", bundle);
        return new MockBundleContext(bundle, createActivator(bundle));
    }

    @Nullable
    Bundle getBundle(long id) {
        logger.info("Get bundle #{}", id);
        return bundles.get(id);
    }

    Bundle[] getBundles() {
        logger.info("Get installed bundles");
        return bundles.values().toArray(MockBundle[]::new);
    }

    @Nullable
    String getProperty(String key) {
        var value = properties.getProperty(key);
        logger.info("Got property {} of value '{}'", key, value);
        return value;
    }

    Stream<MockServiceReference<Object>> references(@Nullable String locator, @Nullable String filter)
            throws InvalidSyntaxException {
        return references(locator == null ? null : type(locator), filter == null ? null : createFilter(filter));
    }

    @SuppressWarnings("unchecked")
    <S> Stream<MockServiceReference<S>> references(@Nullable Class<S> locator, @Nullable Filter filter) {
        logger.info("Get service references by {} locator and {} filter.", locator, filter);
        var registrations = serviceRegistry.stream();
        if (locator != null) {
            registrations = registrations.filter(registration -> registration.locators.contains(locator));
        }
        if (filter != null) {
            registrations = registrations.filter(registration -> filter.matches(registration.properties));
        }
        return registrations.map(registration -> (MockServiceReference<S>) registration.reference);
    }

    <S> ServiceRegistration<S> register(MockServiceRegistration<S> registration) {
        serviceRegistry.add(registration);
        var reference = registration.reference;
        registration.bundle.registeredServices.add(reference);
        var event = new ServiceEvent(ServiceEvent.REGISTERED, reference);
        // TODO filter
        serviceListeners.forEach(filtered -> filtered.listener.serviceChanged(event));
        return registration;
    }

    void unregister(ServiceRegistration<?> registration) {
        // TODO
    }
}
