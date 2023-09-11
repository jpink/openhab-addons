package org.openhab.binding.entsoe.internal.client.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.time.ZonedDateTime;

@XStreamAlias("Publication_MarketDocument")
public class Publication {
    @XStreamAlias("createdDateTime")
    public ZonedDateTime created;
    @XStreamAlias("TimeSeries")
    public TimeSeries timeSeries;

}
