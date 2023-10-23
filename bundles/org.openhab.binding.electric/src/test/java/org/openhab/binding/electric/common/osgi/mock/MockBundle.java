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

import static java.util.Collections.emptyMap;
import static org.openhab.binding.electric.common.Collections.empty;
import static org.openhab.binding.electric.common.Collections.nullify;
import static org.openhab.binding.electric.common.Core.elvis;
import static org.osgi.framework.Constants.BUNDLE_VERSION;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.Version;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bundle mock. Except resource/class loading operations (which are executed on
 * its internal class loader), the rest of the methods are dummies.
 *
 * @author Jukka Papinkivi
 * @see <a href=
 *      "https://stackoverflow.com/questions/62938628/does-each-bundle-deployed-in-osgi-framework-has-its-own-bundlecontext-object">...</a>
 */
@NonNullByDefault({})
public class MockBundle implements Bundle {
    private enum State {
        INSTALLED(Bundle.INSTALLED, true),
        RESOLVED(Bundle.RESOLVED, false),
        STARTING(Bundle.STARTING, false),
        ACTIVE(Bundle.ACTIVE, false),
        STOPPING(Bundle.STOPPING, false),
        UNINSTALLED(Bundle.UNINSTALLED, true);

        public final boolean modified;
        public final int value;

        State(int value, boolean modified) {
            this.value = value;
            this.modified = modified;
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private static class MockBundleStartLevel extends MockBundleReference implements BundleStartLevel {
        private int assigned;
        boolean persistentlyStarted, activationPolicyUsed;

        private MockBundleStartLevel(MockBundle bundle, int initial) {
            super(bundle);
            assigned = initial;
        }

        @Override
        public int getStartLevel() {
            logger.info("Get assigned start level {}.", assigned);
            bundle.notUninstalled();
            return assigned;
        }

        @Override
        public void setStartLevel(int startLevel) {
            logger.info("Assign start level {}. Previous was {}", startLevel, assigned);
            if (startLevel < 1) {
                throw new IllegalArgumentException("Start level must be positive!");
            }
            if (bundle.getBundleId() == 0) {
                throw new IllegalArgumentException("Can't set system bundle start level!");
            }
            bundle.notUninstalled();
            assigned = startLevel;
            // TODO start and stop
        }

        @Override
        public boolean isPersistentlyStarted() {
            logger.info("Is persistently started = {}", persistentlyStarted);
            bundle.notUninstalled();
            return persistentlyStarted;
        }

        @Override
        public boolean isActivationPolicyUsed() {
            logger.info("Is activation policy used = {}", activationPolicyUsed);
            bundle.notUninstalled();
            return activationPolicyUsed;
        }
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    final MockFramework framework;
    private final long id;
    protected final Hashtable<String, @Nullable String> headers;
    final String location;
    private State state = State.INSTALLED;
    private long lastModified = System.currentTimeMillis();
    private final MockBundleStartLevel level;
    public final Set<SynchronousBundleListener> synchronousBundleListeners = new LinkedHashSet<>();
    private final Set<BundleListener> bundleListeners = new LinkedHashSet<>();
    final Set<ServiceReference<?>> registeredServices = new LinkedHashSet<>();
    final Set<ServiceReference<?>> servicesInUse = new LinkedHashSet<>();
    protected @Nullable MockBundleContext context;
    private @Nullable Future<?> task;

    MockBundle(@Nullable MockFramework mock, String location, Hashtable<String, @Nullable String> headers,
            int startLevel) {
        if (mock == null) {
            framework = (MockFramework) this;
            id = 0;
        } else {
            framework = mock;
            id = ++mock.bundleSequence;

        }
        this.headers = headers;
        this.location = location;
        level = new MockBundleStartLevel(this, startLevel);
    }

    // #region Object overrides
    @Override
    public boolean equals(@Nullable Object o) {
        return this == o || o instanceof MockBundle that && id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        var name = headers.get(Constants.BUNDLE_NAME);
        if (name == null) {
            name = getSymbolicName();
        }
        return elvis(name, location) + " bundle";
    }
    // #endregion

    // #region Bundle implementation
    @Override
    public int getState() {
        return state.value;
    }

    @Override
    public void start(int options) throws BundleException {
        switch (options) {
            case START_TRANSIENT -> logger.info("Start transient");
            case START_ACTIVATION_POLICY -> logger.info("Start activation policy");
            default -> logger.info("Start");
        }
        notUninstalled();
        waitTask();
        if (isActive()) {
            return;
        }
        if (!isResolved()) {
            framework.resolve(this);
        }

        switch (state) {
            case ACTIVE -> {
            }
            case RESOLVED -> {
                starting();
                fireBundleEvent(BundleEvent.STARTING);
                var activator = framework.createActivator(this);
                if (activator != null) {
                    try {
                        activator.start(context);
                    } catch (Exception e) {
                        throw new MockFramework.ActivatorError(activator, "Exception on start!", e);
                    }
                }
                active();
                fireBundleEvent(BundleEvent.STARTED);
            }
            default -> throw new IllegalStateException();
        }
    }

    @Override
    public void start() throws BundleException {
        start(0);
    }

    @Override
    public void stop(int options) {
        notUninstalled();
        switch (state) {
            case ACTIVE -> {
                if (options == STOP_TRANSIENT) {
                    logger.info("Stop transient");
                } else {
                    logger.info("Stop");
                }
                fireBundleEvent(BundleEvent.STOPPING);
                var context = this.context;
                if (context == null) {
                    throw new IllegalStateException(); // TODO
                }
                var activator = context.activator;
                if (activator != null) {
                    try {
                        activator.stop(context);
                    } catch (Exception e) {
                        throw new RuntimeException(e); // TODO
                    }
                }
                context.dispose();
                this.context = null;
                resolved();
                fireBundleEvent(BundleEvent.STOPPED);
            }
            case RESOLVED -> {
            }
            default -> throw new IllegalStateException();
        }
    }

    @Override
    public void stop() {
        stop(0);
    }

    @Override
    public void update(@Nullable InputStream input) throws BundleException {
        notUninstalled();
        if (isActive()) {
            logger.info("Update");
            stop();
            if (input == null) {
                logger.info("Updating from default location");
            } else {
                throw new BundleException("Unable to update bundle!");
            }
            installed();
            fireBundleEvent(BundleEvent.UPDATED);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void update() throws BundleException {
        update(null);
    }

    @Override
    public void uninstall() throws BundleException {
        notUninstalled();
        if (isActive()) {
            stop();
        }
        uninstalled();
        framework.bundles.remove(id);
        lastModified = System.currentTimeMillis();
        fireBundleEvent(BundleEvent.UNINSTALLED);
    }

    @Override
    @NonNullByDefault({})
    public Dictionary<String, String> getHeaders() {
        return headers;
    }

    @Override
    public long getBundleId() {
        return id;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public @Nullable ServiceReference<?>[] getRegisteredServices() {
        logger.info("Get registered services.");
        notUninstalled();
        return nullify(registeredServices, ServiceReference<?>[]::new);
    }

    @Override
    public @Nullable ServiceReference<?>[] getServicesInUse() {
        logger.info("Get services in use.");
        notUninstalled();
        return nullify(servicesInUse, ServiceReference<?>[]::new);
    }

    @Override
    public boolean hasPermission(Object permission) {
        logger.info("Has permission {}", permission);
        notUninstalled();
        return true;
    }

    @Override
    public @Nullable URL getResource(String name) {
        logger.info("Get resource '{}'.", name);
        notUninstalled();
        return loader().getResource(name);
    }

    @Override
    public Dictionary<String, String> getHeaders(@Nullable String locale) {
        return headers;
    }

    @Override
    public @Nullable String getSymbolicName() {
        return headers.get(Constants.BUNDLE_SYMBOLICNAME);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        logger.info("Load class {}.", name);
        notUninstalled();
        return loader().loadClass(name);
    }

    @Override
    public @Nullable Enumeration<URL> getResources(String name) throws IOException {
        logger.info("Get resources '{}'.", name);
        notUninstalled();
        return loader().getResources(name);
    }

    @Override
    public @Nullable Enumeration<String> getEntryPaths(String path) {
        logger.info("Get entry paths '{}'.", path);
        notUninstalled();
        return null; // emptyEnumeration();
    }

    @Override
    public @Nullable URL getEntry(String name) {
        logger.info("Get entry '{}'.", name);
        notUninstalled();
        return getResource(name);
    }

    @Override
    public long getLastModified() {
        return lastModified;
    }

    @Override
    public @Nullable Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
        logger.info("Find entries from {} of {}.", path, filePattern);
        notUninstalled();
        Enumeration<URL> enumeration = null;
        try {
            enumeration = getResources(path + "/" + filePattern);
        } catch (IOException e) {
            // catch to allow nice behavior
            System.err.println("returning an empty enumeration as cannot load resource; exception " + e);
        }
        return empty(enumeration);
    }

    @Override
    public @Nullable MockBundleContext getBundleContext() {
        return context;
    }

    @Override
    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int signersType) {
        logger.info("Get signer certificates of {} type", signersType);
        switch (signersType) {
            case SIGNERS_ALL, SIGNERS_TRUSTED -> {
                return emptyMap();
            }
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public Version getVersion() {
        var version = headers.get(BUNDLE_VERSION);
        return version == null ? Version.emptyVersion : new Version(version);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> A adapt(Class<A> type) {
        logger.info("Adapt to {}", type);
        if (BundleStartLevel.class.equals(type)) {
            return (A) level;
        }
        try {
            return type.cast(this);
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public @Nullable File getDataFile(String filename) {
        logger.info("Get data file {}", filename);
        notUninstalled();
        return null;
    }

    // #region Comparable<Bundle> implementation
    @Override
    public int compareTo(Bundle o) {
        return (int) (id - o.getBundleId());
    }
    // #endregion
    // #endregion

    void add(BundleListener listener) {
        if (listener instanceof SynchronousBundleListener synchronous) {
            logger.info("Add synchronous bundle listener {}.", synchronous);
            synchronousBundleListeners.add(synchronous);
        } else {
            logger.info("Add asynchronous bundle listener {}.", listener);
            bundleListeners.add(listener);
        }
    }

    void remove(BundleListener listener) {
        if (listener instanceof SynchronousBundleListener synchronous) {
            logger.info("Remove synchronous bundle listener {}.", synchronous);
            synchronousBundleListeners.remove(synchronous);
        } else {
            logger.info("Remove asynchronous bundle listener {}.", listener);
            bundleListeners.remove(listener);
        }
    }

    public void notUninstalled() {
        logger.info("Ensuring that the bundle isn't uninstalled.");
        if (isUninstalled())
            throw new IllegalStateException("The bundle is uninstalled!");
    }

    protected void waitTask() {
        var task = this.task;
        if (task != null) {
            logger.info("Waiting task!");
            try {
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Task failed!", e);
            }
        }
    }

    private void fireBundleEvent(int type) {
        logger.info("Fire bundle event #{}", type);
        var event = new BundleEvent(type, this);
        bundleListeners.forEach(listener -> listener.bundleChanged(event));
    }

    private boolean is(State value) {
        var current = state;
        if (current == value) {
            logger.info("State is " + value + ".");
            return true;
        } else {
            logger.info("State " + current + " isn't " + value + ".");
            return false;
        }
    }

    protected boolean isInstalled() {
        return is(State.INSTALLED);
    }

    protected boolean isResolved() {
        return is(State.RESOLVED);
    }

    protected boolean isStarting() {
        return is(State.STARTING);
    }

    protected boolean isStartingActiveOrStopping() {
        switch (state) {
            case STARTING, ACTIVE, STOPPING -> {
                logger.info("State {} is in starting, active or stopping.", state);
                return true;
            }
            default -> {
                logger.info("State {} isn't in starting, active or stopping.", state);
                return false;
            }
        }
    }

    protected boolean isActive() {
        return is(State.ACTIVE);
    }

    protected boolean isStopping() {
        return is(State.STOPPING);
    }

    protected boolean isUninstalled() {
        return is(State.UNINSTALLED);
    }

    private void set(State value) {
        var old = state;
        if (old == value) {
            logger.warn("The {} state not changed!", value);
        } else {
            state = value;
            if (value.modified) {
                lastModified = System.currentTimeMillis();
            }
            logger.info("State changed from {} to {}.", old, value);
        }
    }

    protected void installed() {
        set(State.INSTALLED);
    }

    void resolved() {
        set(State.RESOLVED);
    }

    protected void starting() {
        set(State.STARTING);
    }

    protected void active() {
        set(State.ACTIVE);
    }

    protected void stopping() {
        set(State.STOPPING);
    }

    protected void uninstalled() {
        set(State.UNINSTALLED);
    }

    private ClassLoader loader() {
        logger.info("Get class loader. Returning system class loader.");
        return ClassLoader.getSystemClassLoader();
    }

    public MockBundleContext context() {
        var context = getBundleContext();
        if (context == null) {
            throw new IllegalStateException();
        }
        return context;
    }
}
