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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;

/**
 * Service objects mock.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class MockServiceObjects<S> extends MockBundleReference implements ServiceObjects<S> {
    private final MockServiceReference<S> reference;
    private final MockBundleContext bundleContext;
    private int useCount = 0;
    @Nullable S cached;

    protected MockServiceObjects(MockServiceReference<S> reference, MockBundleContext bundleContext) {
        super(bundleContext.bundle);
        this.reference = reference;
        this.bundleContext = bundleContext;
    }

    @Override
    public S getService() {
        ensureValid();
        if (unregistered()) {
            return null;
        }
        cached = reference.registration.getService(this);
        if (++useCount == 1) {
            bundle.servicesInUse.add(reference);
        }
        return cached;
    }

    @Override
    public void ungetService(S service) {
        ensureValid();
        if (service == null) {
            throw new IllegalArgumentException("The specified service object is null!");
        }
        if (unregistered() || useCount == 0) {
            return;
        }
        if (useCount == 1) {
            reference.registration.releaseService(bundle, cached());
            bundle.servicesInUse.remove(reference);
        }
        --useCount;
    }

    @Override
    public ServiceReference<S> getServiceReference() {
        return reference;
    }

    S cached() {
        if (cached == null) {
            throw new IllegalStateException();
        }
        return cached;
    }

    private void ensureValid() {
        bundleContext.ensureValid();
    }

    boolean releaseService() {
        if (useCount == 0 || unregistered()) {
            ensureValid();
            return false;
        }
        ungetService(cached);
        return true;
    }

    private boolean unregistered() {
        return reference.registration.unregistered;
    }

    boolean using() {
        return useCount > 0;
    }
}
