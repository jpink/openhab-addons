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
package org.openhab.binding.electric.internal.handler.price;

import static org.openhab.binding.electric.internal.imp.common.Time.convert;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.EURO_CENT_PER_KILOWATT_HOUR;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.EURO_PER_MEGAWATT_HOUR;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.energyPriceUnit;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.moneyCentUnit;
import static org.openhab.binding.electric.internal.imp.monetary.Monetary.taxPriceOfSum;
import static org.openhab.binding.electric.internal.old.Constants.UNIT_CENT_PER_KWH;
import static org.openhab.binding.electric.internal.old.Constants.UNIT_CURRENCY_PER_MWH;
import static org.openhab.core.library.unit.Units.KILOWATT_HOUR;
import static org.openhab.core.library.unit.Units.MEGAWATT_HOUR;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Locale;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Energy;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.electric.internal.handler.price.service.CurrencyMismatch;
import org.openhab.binding.electric.internal.imp.monetary.EnergyPrice;
import org.openhab.binding.electric.internal.imp.monetary.Monetary;
import org.openhab.binding.electric.internal.imp.monetary.TaxPrice;

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

    public transient ZoneId zone;

    /** Currency code. */
    public @Nullable String currency;

    public transient Currency responseCurrency = Currency.getInstance(Locale.getDefault());

    /** Unit used in config and display. */
    public String unit;

    public transient Unit<Energy> responseMeasure = MEGAWATT_HOUR;

    public transient Unit<EnergyPrice> spotUnit = EURO_PER_MEGAWATT_HOUR;

    public transient Unit<EnergyPrice> targetUnit = EURO_CENT_PER_KILOWATT_HOUR;

    /** The fixed electricity transfer fee including value added tax. */
    public BigDecimal transfer;

    /** The fixed energy tax amount including value added tax. */
    public BigDecimal tax;

    /** The fixed sellers margin price including value added tax. */
    public BigDecimal margin;

    /** General VAT percent */
    public int general;

    /** Electricity sellers VAT percent */
    public int seller;

    public int precision = 7;

    /** The fraction digits of the value. */
    public int scale = 2;

    public PriceConfig() {
        this(ZoneId.systemDefault(), UNIT_CURRENCY_PER_MWH, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0);
    }

    /** Constructor for unit tests. */
    public PriceConfig(ZoneId zone, String unit, BigDecimal transfer, BigDecimal tax, BigDecimal margin, int general,
            int seller) {
        this.zone = zone;
        this.unit = unit;
        this.transfer = transfer;
        this.tax = tax;
        this.margin = margin;
        this.general = general;
        this.seller = seller;
    }

    public ZonedDateTime local(ZonedDateTime time) {
        return convert(time, zone);
    }

    public boolean showInCents() {
        return UNIT_CENT_PER_KWH.equals(unit);
    }

    public Unit<EnergyPrice> response(Currency currency, Unit<Energy> measure) throws CurrencyMismatch {
        responseCurrency = currency;
        responseMeasure = measure;
        if (this.currency != null && Currency.getInstance(this.currency).equals(currency)) {
            throw new CurrencyMismatch(Currency.getInstance(this.currency), currency);
        }
        targetUnit = showInCents() ? energyPriceUnit(moneyCentUnit(currency), KILOWATT_HOUR)
                : energyPriceUnit(currency, MEGAWATT_HOUR);
        return spotUnit = energyPriceUnit(currency, measure);
    }

    public TaxPrice<EnergyPrice> transferTaxPrice() {
        return taxPriceOfSum(transfer, targetUnit, general);
    }

    public TaxPrice<EnergyPrice> taxTaxPrice() {
        return taxPriceOfSum(tax, targetUnit, general);
    }

    public TaxPrice<EnergyPrice> marginTaxPrice() {
        return taxPriceOfSum(margin, targetUnit, seller);
    }

    public Quantity<EnergyPrice> energyPrice(BigDecimal value) {
        return Monetary.energyPrice(value, targetUnit);
    }
}
