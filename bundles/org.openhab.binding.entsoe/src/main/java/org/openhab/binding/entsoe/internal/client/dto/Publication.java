package org.openhab.binding.entsoe.internal.client.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.openhab.binding.entsoe.internal.price.service.Bug;
import org.openhab.binding.entsoe.internal.price.service.DailyCache;
import org.openhab.binding.entsoe.internal.price.service.PriceDetails;

@XStreamAlias("Publication_MarketDocument")
public class Publication extends MarketDocument {

    @XStreamAlias("TimeSeries")
    public TimeSeries timeSeries;

    public DailyCache toDailyCache(PriceDetails details) throws Bug {
        try {
            return timeSeries.toDailyCache(details, created.withZoneSameInstant(details.zone()));
        } catch (Throwable t) {
            throw new Bug(t);
        }
    }

}
