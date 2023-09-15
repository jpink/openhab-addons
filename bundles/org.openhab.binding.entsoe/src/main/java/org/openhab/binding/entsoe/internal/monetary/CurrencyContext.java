package org.openhab.binding.entsoe.internal.monetary;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;

import org.eclipse.jdt.annotation.NonNull;

public interface CurrencyContext {
    Currency getCurrency();

    /** How many fraction digits, the value is rounded. Negative value means no rounding. */
    int getFractionDigits();

    MathContext getMathContext();

    default int getPrecision() {
        return getMathContext().getPrecision();
    }

    default RoundingMode getRoundingMode() {
        return getMathContext().getRoundingMode();
    }

    String format(@NonNull BigDecimal value);

    BigDecimal round(@NonNull BigDecimal value);
}
