package org.openhab.binding.entsoe.internal.client.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Point {
    public Integer position;

    @XStreamAlias("price.amount")
    public Double price;

}
