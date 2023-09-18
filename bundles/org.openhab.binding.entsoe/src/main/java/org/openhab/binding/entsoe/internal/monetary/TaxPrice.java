package org.openhab.binding.entsoe.internal.monetary;

import org.eclipse.jdt.annotation.NonNullByDefault;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import static tech.units.indriya.AbstractQuantity.ONE;

/**
 * Taxable price
 */
@NonNullByDefault
@SuppressWarnings("unchecked")
public record TaxPrice<Q extends MonetaryQuantity<Q>>(Quantity<Q> amount, Quantity<Dimensionless> vatRate) {
    public static <Q extends MonetaryQuantity<Q>> TaxPrice<Q> ofTotal(Quantity<Q> total,
            Quantity<Dimensionless> vatRate) {
        return new TaxPrice<>(((Quantity<Q>) total.divide(vatRate.add(ONE))).to(total.getUnit()), vatRate);
    }

    public Quantity<Q> vat() {
        return ((Quantity<Q>) amount.multiply(vatRate)).to(amount.getUnit());
    }

    public Quantity<Q> total() {
        return amount.add(vat());
    }
}
