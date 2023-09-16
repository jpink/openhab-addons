package org.openhab.binding.entsoe.internal.monetary;

import static org.openhab.binding.entsoe.internal.monetary.GenericDimension.MONETARY_ENERGY;
import static org.openhab.core.library.unit.Units.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

/** This unit is currency / energy quotient. */
public class EnergyCurrency extends AbstractCurrencyUnit<EnergyPrice> {
    public final CurrencyUnit currencyUnit;
    public final Unit<Energy> energyUnit;

    public EnergyCurrency(CurrencyUnit currency, Unit<Energy> energy) {
        super(currency.prefix, currency.getCurrency(), currency.getFractionDigits(), currency.getMathContext(),
                currency + "/" + energy, currency.getName() + " per " + energy.getName(), MONETARY_ENERGY);
        currencyUnit = currency;
        energyUnit = energy;
    }

    @Override
    public EnergyCurrency getSystemUnit() {
        return new EnergyCurrency(currencyUnit.getSystemUnit(), energyUnit.getSystemUnit());
    }

    @Override
    public Map<? extends Unit<?>, Integer> getBaseUnits() {
        var map = new HashMap<Unit<?>, Integer>();
        map.put(currencyUnit.getSystemUnit(), 1);
        map.put(WATT, -1);
        map.put(HOUR, -1);
        return Collections.unmodifiableMap(map);
    }
}
