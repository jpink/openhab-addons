package org.openhab.binding.entsoe.internal.client.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Acknowledgement_MarketDocument")
public class Acknowledgement extends MarketDocument {
    @XStreamAlias("Reason")
    public Reason reason;

}
