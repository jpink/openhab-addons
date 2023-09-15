package org.openhab.binding.entsoe.internal.monetary;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.eclipse.jdt.annotation.NonNull;

public class EnergyPrice extends BigDecimalQuantity<EnergyPrice> {

    public EnergyPrice(@NonNull BigDecimal value, @NonNull Unit<EnergyPrice> unit) {
        // TODO Ensure math context
        super(value, unit);
    }

    public EnergyPrice(@NonNull Number value, @NonNull Unit<EnergyPrice> unit) {
        this(value.toString(), unit);
    }

    public EnergyPrice(@NonNull String value, @NonNull Unit<EnergyPrice> unit) {
        this(new BigDecimal(value), unit);
    }

    protected EnergyPrice(@NonNull BigDecimal value, @NonNull MathContext context, @NonNull Unit<EnergyPrice> unit) {
        super(value, context, unit, Scale.RELATIVE);
    }

    @Override
    protected Quantity<EnergyPrice> create(@NonNull BigDecimal value, @NonNull MathContext context,
            @NonNull Unit<EnergyPrice> unit, @NonNull Scale scale) {
        return new EnergyPrice(value, context, unit);
    }
}
