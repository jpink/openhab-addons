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
package org.openhab.binding.electric.common.osgi;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.common.osgi.mock.MockComponentContext;
import org.openhab.binding.electric.common.osgi.mock.MockServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * Abstract OSGi unit tests which needs component context.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public abstract class ComponentTest<I, @Nullable C> extends BundleTest<I> {
    private @Nullable MockComponentContext<C> componentContext;
    private @Nullable C component;

    protected abstract ServiceRegistration<C> registerComponent();

    protected abstract void configureComponent();

    @SuppressWarnings("unchecked")
    protected C getComponent() {
        var component = this.component;
        if (component == null) {
            component = (C) getComponentContext().getComponentInstance().getInstance();
        }
        return component;
    }

    protected MockComponentContext<C> getComponentContext() {
        var context = componentContext;
        if (context == null) {
            context = new MockComponentContext<>(bundleContext,
                    (MockServiceReference<C>) registerComponent().getReference());
            componentContext = context;
            configureComponent();
        }
        return context;
    }

    @Override
    public void tearDown() throws Exception {
        if (component != null) {
            getComponentContext().getComponentInstance().dispose();
        }
        super.tearDown();
    }
}
