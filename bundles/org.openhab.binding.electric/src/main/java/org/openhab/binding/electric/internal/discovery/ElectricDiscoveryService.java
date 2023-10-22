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
package org.openhab.binding.electric.internal.discovery;

import static org.openhab.binding.electric.internal.ElectricBindingConstants.BINDING_ID;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.BRIDGE_TYPE_PRICE;

import java.math.BigDecimal;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.internal.handler.entsoe.dto.Area;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * ENTSO-E discovery service.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery." + BINDING_ID)
@NonNullByDefault
public class ElectricDiscoveryService extends AbstractDiscoveryService {
    @Activate
    public ElectricDiscoveryService(@Reference TranslationProvider i18nProvider, @Reference LocaleProvider localeProvider)
            throws IllegalArgumentException {
        super(Set.of(BRIDGE_TYPE_PRICE), 10, false);
        this.i18nProvider = i18nProvider;
        this.localeProvider = localeProvider;
    }

    /**
     * This method is called by the {@link #startScan(ScanListener))} implementation of the
     * {@link AbstractDiscoveryService}. The abstract class schedules a call of {@link #stopScan()} after
     * {@link #getScanTimeout()} seconds. If this behavior is not appropriate, the {@link #startScan(ScanListener))}
     * method should be overridden.
     */
    @Override
    protected void startScan() {
        var locale = localeProvider.getLocale();
        var area = Area.of(locale);
        if (area == null) {
            return;
        }
        var country = locale.getCountry();
        var builder = DiscoveryResultBuilder.create(new ThingUID(BRIDGE_TYPE_PRICE, country))
                .withProperty("area", area.code).withRepresentationProperty("area");
        if ("FI".equals(country)) {
            builder.withProperty("tax", BigDecimal.valueOf(2.79)); // 2.79372 TODO move to resource bundle
        }
        thingDiscovered(builder.build());
    }
}
