package org.openhab.binding.entsoe.internal.monetary;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.core.library.unit.Units.*;

import java.util.Currency;
import java.util.stream.Stream;

import javax.measure.MetricPrefix;
import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@NonNullByDefault
class CurrencyUnitTest {
    static CurrencyUnit euroCent() {
        return (CurrencyUnit) MetricPrefix.CENTI(euro());
    }

    static CurrencyUnit euro() {
        return new CurrencyUnit(eur());
    }

    static CurrencyUnit euro(int fractionDigits) {
        return new CurrencyUnit(eur(), fractionDigits);
    }

    static Currency eur() {
        return Currency.getInstance("EUR");
    }

    static Stream<Unit<?>> getSymbol_eqToString() {
        return Stream.of(euro(), euroCent(), JOULE, NEWTON, SECOND, WATT);
    }

    @ParameterizedTest
    @MethodSource
    void getSymbol_eqToString(Unit<?> unit) {
        assertEquals(unit.toString(), unit.getSymbol());
    }

    @Test
    void getSymbol_null() {
        assertNull(MetricPrefix.MEGA(WATT).getSymbol());
    }

    @Test
    void getName_euro_euro() {
        assertEquals("euro", euro().getName());
    }

    @Test
    void getName_euroCent_centiEuro() {
        assertEquals("centieuro", euroCent().getName());
    }

    @Test
    void getName_megaEuro_megaEuro() {
        assertEquals("megaeuro", MetricPrefix.MEGA(euro()).getName());
    }

    @Test
    void getSystemUnit_euro_euro() {
        assertEquals(euro(), euro().getSystemUnit());
    }

    @Test
    void getSystemUnit_euroCent_euro() {
        assertEquals(euro(), euroCent().getSystemUnit());
    }

    @Test
    void getCurrency_euro_eur() {
        assertEquals(eur(), euro().getCurrency());
    }

    @Test
    void getFractionDigits_euro_2() {
        assertEquals(2, euro().getFractionDigits());
    }

    @Test
    void isCompatible_same_true() {
        assertTrue(euro().isCompatible(euro()));
    }

    @Test
    void isEquivalentTo_same_true() {
        assertTrue(euro().isEquivalentTo(euro()));
    }

    @Test
    void divide_eurPerMwh_ok() {
        assertEquals("€/MWh", euro().divide(MEGAWATT_HOUR).toString());
    }

    @Test
    void equals_same_true() {
        assertEquals(euro(), euro());
    }

    @Test
    void hashCode_same_true() {
        assertEquals(euro().hashCode(), euro().hashCode());
    }

    @Test
    void toString_euro_e() {
        assertEquals("€", euro().toString());
    }

    @Test
    void prefix_euroCent() {
        assertEquals("c", euroCent().toString());
    }
}
