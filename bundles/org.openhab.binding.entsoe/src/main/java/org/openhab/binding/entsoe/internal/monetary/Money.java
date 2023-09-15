package org.openhab.binding.entsoe.internal.monetary;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNull;

/** Quantity of money in specific currency. */
public class Money extends BigDecimalQuantity<Money> {

    public Money(@NonNull BigDecimal value, @NonNull CurrencyUnit unit) {
        this(value, unit.getMathContext(), unit);
    }

    public Money(@NonNull Number amount, @NonNull CurrencyUnit unit) {
        this(amount.toString(), unit);
    }

    public Money(@NonNull String amount, @NonNull CurrencyUnit unit) {
        this(new BigDecimal(amount, unit.getMathContext()), unit.getMathContext(), unit);
    }

    private Money(@NonNull BigDecimal value, @NonNull MathContext context, @NonNull Unit<Money> unit) {
        super(value, context, unit, Scale.RELATIVE);
    }

    @Override
    protected Quantity<Money> create(@NonNull BigDecimal value, @NonNull MathContext context, @NonNull Unit<Money> unit,
            @NonNull Scale scale) {
        return new Money(value, context, unit);
    }

    @Override
    public String toString() {
        var value = getValue();
        var unit = getUnit();
        if (unit instanceof CurrencyUnit currencyUnit)
            return currencyUnit.format(value);
        else
            return value + " " + unit;
    }
}
