package org.openhab.binding.entsoe.internal.client.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Point {
    public int position;

    @XStreamAlias("price.amount")
    public float price;
}
