package org.openhab.binding.entsoe.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.entsoe.internal.common.Translator;

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
