/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional information.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.entsoe.internal.price;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.entsoe.internal.price.service.CurrencyUnit;
import org.openhab.binding.entsoe.internal.price.service.PriceDetails;
import org.openhab.binding.entsoe.internal.price.service.ProductPrice;
import org.openhab.binding.entsoe.internal.price.service.VatRate;
import org.openhab.core.library.unit.Units;

import java.time.ZoneId;

import static org.openhab.binding.entsoe.internal.Constants.*;

/**
 * The {@link PriceConfig} class contains fields mapping thing configuration parameters.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceConfig {

    /** A security token. */
    public String token = "";

    /** An area EIC code. */
    public String area = "";

    /** Unit used in config and display. */
    public String unit = UNIT_CENT_PER_KWH;

    /** The fixed electricity transfer fee including value added tax. */
    public double transfer;

    /** The fixed energy tax amount including value added tax. */
    public double tax;

    /** The fixed sellers margin price including value added tax. */
    public double margin;

    /** General VAT percent */
    public int general;

    /** Electricity sellers VAT percent */
    public int seller;

    private ProductPrice price(double totalPrice, VatRate vatRate) {
        return ProductPrice.fromTotal(totalPrice, vatRate, new CurrencyUnit(EUR, UNIT_CENT_PER_KWH.equals(unit)),
                Units.KILOWATT_HOUR);
    }

    public PriceDetails toPriceDetails(ZoneId zone) {
        var general = new VatRate(this.general);
        var seller = new VatRate(this.seller);
        return new PriceDetails(zone, price(transfer, general), price(tax, general), seller, price(margin, seller));
    }

}
