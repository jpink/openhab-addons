package org.openhab.binding.entsoe.internal.client.dto;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.openhab.binding.entsoe.internal.price.service.*;

import javax.measure.Unit;
import javax.measure.quantity.Energy;

public class Period {
    public TimeInterval timeInterval;
    public Duration resolution;

    @XStreamImplicit(itemFieldName = "Point")
    public List<Point> points;

    public DailyCache toDailyCache(PriceDetails details, ZonedDateTime created, String domain, Currency currency,
            Unit<Energy> measure) {
        // 1. Convert spot prices
        var margin = details.margin();
        var spotCurrency = margin.currency();
        var spotUnit = margin.unit();
        // Conversion direction seems to be wrong but result is now correct!
        var currencyConverter = spotCurrency.getConverterTo(new CurrencyUnit(currency, false));
        var unitConverter = spotUnit.getConverterTo(measure);
        var sellersVatRate = details.sellersVatRate();
        var spotPrices = points.stream().map(point -> {
            var spotAmount = unitConverter.convert(currencyConverter.convert(point.amount));
            return ProductPrice.fromPrice(spotAmount, sellersVatRate, spotCurrency, spotUnit);
        }).toList();

        // 2. Sum total prices
        var transfer = details.transfer();
        var tax = details.tax();
        var additions = transfer.plus(tax).plus(margin);
        var totalPrices = spotPrices.stream().map(spot -> spot.plus(additions)).toList();

        // 3. Count ranks
        var ranks = totalPrices.stream().mapToDouble(ProductPrice::total).sorted().toArray();

        // 4. Count min, average and max
        var statistics = totalPrices.stream().mapToDouble(ProductPrice::total).summaryStatistics();

        // 5. Count normalized values (0.0 - 1.0) which can be shown as a percent.
        // https://stats.stackexchange.com/questions/70801/how-to-normalize-data-to-0-1-range
        var min = statistics.getMin();
        var divider = statistics.getMax() - min;
        var normalizedPrices = totalPrices.stream().map(total -> (total.total() - min) / divider).toList();

        // 6. Wrap everything up in electricity price.
        var minutes = resolution.toMinutes();
        var zone = details.zone();
        var endOffset = timeInterval.start.withZoneSameInstant(zone);
        var startOffset = endOffset.minusMinutes(minutes);
        var electricityPrices = points.stream().map(point -> {
            var position = point.position;
            var start = startOffset.plusMinutes(position * minutes);
            var end = endOffset.plusMinutes(position * minutes);
            var index = position - 1;
            var spot = spotPrices.get(index);
            var total = totalPrices.get(index);
            var rank = Arrays.binarySearch(ranks, total.total()) + 1;
            var normalized = normalizedPrices.get(index);
            return new ElectricityPrice(start, end, transfer, tax, spot, margin, total, rank, normalized,
                    new Holder<>(0), new Holder<>(0.0));
        }).toList();

        return new DailyCache(created, domain, endOffset, timeInterval.end.withZoneSameInstant(zone), resolution,
                currency, measure, electricityPrices, statistics);
    }

}
