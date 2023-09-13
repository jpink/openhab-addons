package org.openhab.binding.entsoe.internal.client.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Publication_MarketDocument")
public class PublicationMarket {
    @XStreamAlias("TimeSeries")
    public TimeSeries timeSeries;
}
