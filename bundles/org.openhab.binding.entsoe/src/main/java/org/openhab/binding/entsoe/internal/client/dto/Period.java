package org.openhab.binding.entsoe.internal.client.dto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.eclipse.jdt.annotation.NonNull;

public class Period {
    public TimeInterval timeInterval;
    public Duration resolution;

    @XStreamImplicit(itemFieldName = "Point")
    public List<Point> points;

    @XStreamOmitField
    private List<Float> prices = null;

    public @NonNull List<Float> getPrices() {
        if (prices == null) {
            prices = new ArrayList<>(points.size());
            for (int index = 0; index < points.size(); index++) {
                var point = points.get(index);
                if (index + 1 != point.position) throw new IllegalStateException("Point position " + point.position + " doesn't match its index " + index + "!");
                prices.add(point.price);
            }
        }
        return prices;
    }
}
