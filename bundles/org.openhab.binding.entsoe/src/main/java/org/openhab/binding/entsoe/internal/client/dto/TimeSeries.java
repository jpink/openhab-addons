package org.openhab.binding.entsoe.internal.client.dto;

import java.util.Currency;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TimeSeries {
    @XStreamAlias("in_Domain.mRID")
    public String domain;

    @XStreamAlias("Period")
    public Period period;

    @XStreamAlias("currency_Unit.name")
    public Currency currency;

    @XStreamAlias("price_Measure_Unit.name")
    public String measure;

}
