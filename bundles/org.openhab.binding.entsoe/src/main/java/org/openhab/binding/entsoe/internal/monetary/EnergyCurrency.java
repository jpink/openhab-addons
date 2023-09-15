package org.openhab.binding.entsoe.internal.monetary;

import static org.openhab.binding.entsoe.internal.monetary.GenericDimension.MONETARY_ENERGY;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

/** This unit is currency / energy quotient. */
public class EnergyCurrency extends AbstractCurrencyUnit<EnergyPrice> {

    @SuppressWarnings("unchecked")
    public static EnergyCurrency ofQuotient(AbstractCurrencyUnit<?> currencyUnit, Unit<?> energyUnit) {
        if (MONETARY_ENERGY.equals(currencyUnit.getDimension().divide(energyUnit.getDimension())))
            return new EnergyCurrency(currencyUnit, (Unit<Energy>) energyUnit);
        throw new IllegalArgumentException();
    }

    public final AbstractCurrencyUnit<?> currencyUnit;
    public final Unit<Energy> energyUnit;

    private EnergyCurrency(AbstractCurrencyUnit<?> currencyUnit, Unit<Energy> energyUnit) {
        super(currencyUnit.getCurrency(), currencyUnit.getFractionDigits(), currencyUnit.getMathContext(),
                currencyUnit.getSymbol() + "/" + energyUnit.getSymbol(),
                currencyUnit.getName() + " per " + energyUnit.getName(), MONETARY_ENERGY);
        this.currencyUnit = currencyUnit;
        this.energyUnit = energyUnit;
    }
}
