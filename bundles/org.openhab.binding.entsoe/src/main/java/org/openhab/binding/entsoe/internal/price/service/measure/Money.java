package org.openhab.binding.entsoe.internal.price.service.measure;

import javax.measure.Unit;
import java.io.Serial;
import java.math.BigDecimal;

/** Quantity of money in specific currency. */
public class Money extends AbstractNumberQuantity<Money> {

    @Serial
    private static final long serialVersionUID = 4177644565609201471L;

    /**
     * @param number The money amount
     * @param unit
     */
    public Money(BigDecimal number, Unit<Money> unit) {
        super(number, unit, Scale.ABSOLUTE);//TODO Scale.RELATIVE);
    }

    @Override
    public BigDecimal getValue() {
        return (BigDecimal) super.getValue();
    }

    @Override
    public String toString() {
        // TODO Workaround for: java.lang.IllegalArgumentException: Cannot format given Object as a Unit
        //      at tech.units.indriya.format.SimpleUnitFormat$DefaultFormat.format(SimpleUnitFormat.java:907)
        return getValue() + " " + getUnit();
    }

}
