package org.openhab.binding.entsoe.internal.client.dto;

import java.time.ZonedDateTime;
import java.util.Currency;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.NotImplementedException;
import org.openhab.binding.entsoe.internal.price.service.DailyCache;
import org.openhab.binding.entsoe.internal.price.service.PriceDetails;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

public class TimeSeries {
    @XStreamAlias("in_Domain.mRID")
    public String domain;

    @XStreamAlias("Period")
    public Period period;

    @XStreamAlias("currency_Unit.name")
    public Currency currency;

    @XStreamAlias("price_Measure_Unit.name")
    public Unit<Energy> measure;

    public DailyCache toDailyCache(PriceDetails details, ZonedDateTime created) {
        return period.toDailyCache(details, created, domain, currency, measure);
    }

}
