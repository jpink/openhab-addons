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
package org.openhab.binding.electric.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.electric.common.Translator;

/**
 * Translations.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public interface Translations extends Translator {
    default String getGraph(String key) {
        return getText("graph." + key);
    }

    default String getPrice(String key) {
        return getThing("config.entsoe.price." + key);
    }

    default String getPriceLabel(String key) {
        return getPrice(key + ".label");
    }

    default String getThing(String key) {
        return getText("thing-type." + key);
    }

    default String electricityPrice() {
        return getThing("entsoe.price.label");
    }

    default String centPerKilowattHour() {
        return getPrice("unit.option.c/kWh");
    }

    default String margin() {
        return getPriceLabel("margin");
    }

    default String noData() {
        return getGraph("no-data");
    }

    default String spot() {
        return getGraph("spot");
    }

    default String tax() {
        return getPriceLabel("tax");
    }

    default String transfer() {
        return getPriceLabel("transfer");
    }

    default String vat() {
        return getGraph("vat");
    }
}
