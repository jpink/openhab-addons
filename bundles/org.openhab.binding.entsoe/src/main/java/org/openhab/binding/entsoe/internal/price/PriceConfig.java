/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.entsoe.internal.price;

import static org.openhab.binding.entsoe.internal.Constants.*;
import static org.openhab.binding.entsoe.internal.monetary.Monetary.*;
import static org.openhab.core.library.unit.Units.MEGAWATT_HOUR;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Locale;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.entsoe.internal.monetary.EnergyPrice;
import org.openhab.binding.entsoe.internal.monetary.Money;
import org.openhab.binding.entsoe.internal.monetary.TaxPrice;
import org.openhab.binding.entsoe.internal.price.service.*;

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

    public transient ZoneId zone = ZoneId.systemDefault();

    /** Currency code. */
    public @Nullable String currency;

    public transient Currency responseCurrency = Currency.getInstance(Locale.getDefault());

    public transient Unit<Money> responseMoney = EUR;

    /** Unit used in config and display. */
    public String unit = UNIT_CURRENCY_PER_MWH;

    public transient Unit<Energy> responseMeasure = MEGAWATT_HOUR;

    public transient Unit<EnergyPrice> priceUnit = EURO_PER_MEGAWATT_HOUR;

    /** The fixed electricity transfer fee including value added tax. */
    public BigDecimal transfer = BigDecimal.ZERO;

    /** The fixed energy tax amount including value added tax. */
    public BigDecimal tax = BigDecimal.ZERO;

    /** The fixed sellers margin price including value added tax. */
    public BigDecimal margin = BigDecimal.ZERO;

    /** General VAT percent */
    public int general;

    /** Electricity sellers VAT percent */
    public int seller;

    public int precision = 7;

    /** The fraction digits of the value. */
    public int scale = 2;

    public ZonedDateTime local(ZonedDateTime instant) {
        return instant.withZoneSameInstant(zone);
    }

    public Unit<EnergyPrice> response(Currency currency, Unit<Energy> measure) throws CurrencyMismatch {
        responseCurrency = currency;
        responseMeasure = measure;
        if (this.currency != null && Currency.getInstance(this.currency) != currency)
            throw new CurrencyMismatch(Currency.getInstance(this.currency), currency);
        return priceUnit = energyPriceUnit(currency, measure);
    }

    public TaxPrice<EnergyPrice> transferTaxPrice() {
        return taxPriceOfSum(transfer, priceUnit, general);
    }

    public TaxPrice<EnergyPrice> taxTaxPrice() {
        return taxPriceOfSum(tax, priceUnit, general);
    }

    public TaxPrice<EnergyPrice> marginTaxPrice() {
        return taxPriceOfSum(margin, priceUnit, seller);
    }
}
