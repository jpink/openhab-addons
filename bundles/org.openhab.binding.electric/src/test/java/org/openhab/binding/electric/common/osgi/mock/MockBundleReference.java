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
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract bundle reference mock.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class MockBundleReference implements BundleReference {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    final MockBundle bundle;

    protected MockBundleReference(MockBundle bundle) {
        this.bundle = bundle;
    }

    protected MockBundleReference(MockBundleReference reference) {
        this(reference.bundle);
    }

    long bundleId() {
        return bundle.getBundleId();
    }

    MockFramework framework() {
        return bundle.framework;
    }

    @Override
    public @Nullable Bundle getBundle() {
        logger.info("Get bundle");
        return bundle;
    }

    @Override
    public String toString() {
        return bundle.toString();
    }
}
