package org.openhab.binding.entsoe.internal.monetary;

import org.eclipse.jdt.annotation.NonNullByDefault;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

@NonNullByDefault
@SuppressWarnings("unchecked")
public record Price<Q extends MonetaryQuantity<Q>>(Quantity<Q> amount, Quantity<Dimensionless> vatRate) {
    /*public static <Q extends MonetaryQuantity<Q>> Price<Q> fromTotal(Quantity<Q> total, Quantity<Dimensionless> vatRate) {
        vatRate.
    }*/

    public Quantity<Q> vat() {
        return ((Quantity<Q>) amount.multiply(vatRate)).to(amount.getUnit());
    }

    public Quantity<Q> total() {
        return amount.add(vat());
    }
}
