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

import static org.openhab.binding.electric.common.Text.isBlank;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.BINDING_ID;
import static org.openhab.binding.electric.internal.ElectricBindingConstants.BRIDGE_TYPE_PRICE;
import static org.openhab.core.library.unit.Units.KILOWATT_HOUR;
import static org.openhab.core.library.unit.Units.MEGAWATT_HOUR;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.i18n.LocaleProvider;
import org.openhab.core.i18n.TranslationProvider;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Electric discovery service.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery." + BINDING_ID)
@NonNullByDefault
public class ElectricDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Activate
    public ElectricDiscoveryService(@Reference TranslationProvider i18nProvider,
            @Reference LocaleProvider localeProvider) throws IllegalArgumentException {
        super(Set.of(BRIDGE_TYPE_PRICE), 0);
        this.i18nProvider = i18nProvider;
        this.localeProvider = localeProvider;
    }

    @Override
    protected void startBackgroundDiscovery() {
        startScan();
    }

    @Override
    protected void startScan() {
        var locale = localeProvider.getLocale();
        var country = locale.getCountry();
        if (isBlank(country)) {
            locale = Locale.getDefault();
            country = locale.getCountry();
        }
        var currency = Currency.getInstance(locale);
        var currencyCode = currency.getCurrencyCode();
        var subunit = switch (currencyCode) {
            case "AUD", "USD" -> "¢";
            case "EUR" -> "c";
            case "GBP" -> "p";
            default -> null;
        };
        var energy = subunit == null ? MEGAWATT_HOUR : KILOWATT_HOUR;
        var tax = 0.0;
        var vat = 0;
        switch (country) {
            case "FI" -> {
                subunit = "snt";
                tax = 2.79; // 2,79372 snt/kWh
                vat = 24;
            }
            case "SE" -> {
                tax = 3.56; // 35,6 Swedish cent/kWh
                vat = 25;
            }
            default -> logger.warn("No parameters defined for {}!", locale.getDisplayCountry());
        }
        var builder = DiscoveryResultBuilder.create(new ThingUID(BRIDGE_TYPE_PRICE, "local"))
                .withProperty("currency", currencyCode)
                .withRepresentationProperty("currency")
                .withProperty("energy", energy.toString());
        if (subunit != null) {
            builder.withProperty("subunit", subunit);
        }
        if (tax > 0.0) {
            builder.withProperty("tax", BigDecimal.valueOf(tax));
        }
        if (vat > 0) {
            builder.withProperty("vat", vat);
        }
        thingDiscovered(builder.build());
    }
}
