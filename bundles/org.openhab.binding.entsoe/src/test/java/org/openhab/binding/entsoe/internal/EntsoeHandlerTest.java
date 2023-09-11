package org.openhab.binding.entsoe.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.price.PriceHandler;

public class EntsoeHandlerTest {
    private void assertDelay(int expected, int resolution, int now) {
        Assertions.assertEquals(expected, PriceHandler.countInitialMinutes(resolution, now));
    }

    private void assertDelayLessThan(int resolution) {
        for (int now = 0; now < 60; now++) {
            Assertions.assertTrue(PriceHandler.countInitialMinutes(resolution, now) < resolution);
        }
    }

    @Test
    public void countInitialMinutes_15all_max14() {
        assertDelayLessThan(15);
    }

    @Test
    public void countInitialMinutes_15now0_0() {
        assertDelay(0, 15, 0);
    }

    @Test
    public void countInitialMinutes_15now1_14() {
        assertDelay(14, 15, 1);
    }

    @Test
    public void countInitialMinutes_15now2_13() {
        assertDelay(13, 15, 2);
    }

    @Test
    public void countInitialMinutes_15now14_1() {
        assertDelay(1, 15, 14);
    }

    @Test
    public void countInitialMinutes_15now15_0() {
        assertDelay(0, 15, 15);
    }

    @Test
    public void countInitialMinutes_15now16_14() {
        assertDelay(14, 15, 16);
    }

    @Test
    public void countInitialMinutes_15now29_1() {
        assertDelay(1, 15, 29);
    }

    @Test
    public void countInitialMinutes_15now30_0() {
        assertDelay(0, 15, 30);
    }

    @Test
    public void countInitialMinutes_15now31_14() {
        assertDelay(14, 15, 31);
    }

    @Test
    public void countInitialMinutes_15now44_1() {
        assertDelay(1, 15, 44);
    }

    @Test
    public void countInitialMinutes_15now45_0() {
        assertDelay(0, 15, 45);
    }

    @Test
    public void countInitialMinutes_15now46_14() {
        assertDelay(14, 15, 46);
    }

    @Test
    public void countInitialMinutes_15now59_1() {
        assertDelay(1, 15, 59);
    }

    @Test
    public void countInitialMinutes_60all_max59() {
        assertDelayLessThan(60);
    }

    @Test
    public void countInitialMinutes_60now0_0() {
        assertDelay(0, 60, 0);
    }

    @Test
    public void countInitialMinutes_60now1_59() {
        assertDelay(59, 60, 1);
    }

    @Test
    public void countInitialMinutes_60now2_58() {
        assertDelay(58, 60, 2);
    }

    @Test
    public void countInitialMinutes_60now30_30() {
        assertDelay(30, 60, 30);
    }

    @Test
    public void countInitialMinutes_60now58_2() {
        assertDelay(2, 60, 58);
    }

    @Test
    public void countInitialMinutes_60now59_1() {
        assertDelay(1, 60, 59);
    }

}
