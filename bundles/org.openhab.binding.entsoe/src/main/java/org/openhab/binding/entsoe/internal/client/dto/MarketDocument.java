package org.openhab.binding.entsoe.internal.client.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.time.ZonedDateTime;

abstract public class MarketDocument {
    @XStreamAlias("createdDateTime")
    public ZonedDateTime created;

}
