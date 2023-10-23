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

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;

/**
 * Component context and instance mock.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault({})
public record MockComponentContext<@Nullable C> (MockBundleContext context,
        @Nullable MockServiceReference<C> reference) implements ComponentContext, ComponentInstance<C> {

    @Override
    public Dictionary<String, Object> getProperties() {
        var reference = reference();
        return reference == null ? new Hashtable<>() : reference.getProperties();
    }

    @Override
    public <S> S locateService(String name) {
        throw new UnsupportedOperationException(name);
    }

    @Override
    public <S> S locateService(String name, ServiceReference<S> reference) {
        throw new UnsupportedOperationException(name);
    }

    @Override
    public Object[] locateServices(String name) {
        throw new UnsupportedOperationException(name);
    }

    @Override
    public BundleContext getBundleContext() {
        return context;
    }

    @Override
    public Bundle getUsingBundle() {
        return context.bundle;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> ComponentInstance<S> getComponentInstance() {
        return (ComponentInstance<S>) this;
    }

    @Override
    public void enableComponent(String name) {
        throw new UnsupportedOperationException(name);
    }

    @Override
    public void disableComponent(String name) {
        throw new UnsupportedOperationException(name);
    }

    @Override
    public @Nullable ServiceReference<C> getServiceReference() {
        return reference;
    }

    // #region Component instance implementation
    @Override
    public void dispose() {
        var reference = reference();
        if (reference != null) {
            reference.releaseService(context);
        }
    }

    @Override
    public @Nullable C getInstance() {
        var reference = reference();
        return reference == null ? null : reference.getService(context);
    }
    // #endregion
}
