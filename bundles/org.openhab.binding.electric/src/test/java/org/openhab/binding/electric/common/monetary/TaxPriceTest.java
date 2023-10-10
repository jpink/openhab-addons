package org.openhab.binding.electric.common.monetary;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.openhab.binding.electric.common.monetary.Monetary.EUR;
import static org.openhab.binding.electric.common.monetary.Monetary.ZERO;
import static org.openhab.binding.electric.common.monetary.Monetary.taxPrice;
import static org.openhab.binding.electric.common.monetary.Monetary.taxPriceOfSum;

@NonNullByDefault
class TaxPriceTest {
    static final TaxPrice<Money> BY_AMOUNT = taxPrice(100, EUR, 24),//
            BY_SUM = taxPriceOfSum(124, EUR, 24);

    @Test
    void amountWhenTrailingZerosThenEqual() {
        assertEquals(
                taxPrice(3.4, EUR, ZERO).amount(),
                taxPriceOfSum(3.4, EUR, ZERO).amount());
    }

    @Test
    void equalsWhenOtherByAmountThenTrue() {
        assertEquals(BY_SUM, BY_AMOUNT);
    }

    @Test
    void equalsWhenOtherBySumThenTrue() {
        assertEquals(BY_AMOUNT, BY_SUM);
    }
}