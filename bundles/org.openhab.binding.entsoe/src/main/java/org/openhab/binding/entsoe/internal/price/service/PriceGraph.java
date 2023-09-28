package org.openhab.binding.entsoe.internal.price.service;

import java.awt.*;
import java.io.Serial;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.text.G2TextMeasurer;
import org.jfree.chart.text.TextBlock;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.svg.SVGGraphics2D;
import org.openhab.binding.entsoe.internal.Translations;
import org.openhab.binding.entsoe.internal.price.PriceConfig;

@NonNullByDefault
public class PriceGraph {
    private static class ZonedDateTimeAxis extends CategoryAxis {
        @Serial
        private static final long serialVersionUID = -7835789623702120751L;

        @Override
        protected TextBlock createLabel(@Nullable Comparable category, float width, @Nullable RectangleEdge edge, @Nullable Graphics2D g2) {
            return TextUtils.createTextBlock(
                    (category instanceof ZonedDateTime time) ? Integer.toString(time.getHour()) : "?",
                    getTickLabelFont(category),
                    getTickLabelPaint(category),
                    width,
                    1,
                    new G2TextMeasurer(g2)
            );
        }
    }

    private static final CategoryAxis X_AXIS = new ZonedDateTimeAxis();

    static {
        X_AXIS.setCategoryMargin(0.0);
        X_AXIS.setLowerMargin(0.0);
        X_AXIS.setUpperMargin(0.0);
        X_AXIS.setMaximumCategoryLabelLines(2);
        X_AXIS.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
    }

    private final PriceCache cache;
    private final PriceConfig config;
    private final Translations t;
    private final String margin, spot, tax, transfer, vat;
    private final DateTimeFormatter dateFormat;

    public PriceGraph(PriceCache cache, Translations translations) {
        this.cache = cache;
        config = cache.config;
        t = translations;
        dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(translations.getLocale());
        margin = translations.margin();
        spot = translations.spot();
        tax = translations.tax();
        transfer = translations.transfer();
        vat = translations.vat();
    }

    private CategoryDataset createDataset() {
        var dataset = new DefaultCategoryDataset();
        cache.electricityPrices.forEach(price -> {
            var column = price.start();
            dataset.addValue(price.vat(), vat, column);
            dataset.addValue(price.transfer().amountValue(), transfer, column);
            dataset.addValue(price.spot().amountValue(), spot, column);
            dataset.addValue(price.margin().amountValue(), margin, column);
            dataset.addValue(price.tax().amountValue(), tax, column);
        });
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        var chart = ChartFactory.createStackedBarChart(
                t.electricityPrice() + " " + dateFormat.format(cache.start.toLocalDate()),
                null,
                config.showInCents() ? t.centPerKilowattHour() : config.targetUnit.toString(),
                dataset,
                PlotOrientation.VERTICAL, true, false, false
        );
        var plot = chart.getCategoryPlot();
        plot.setDomainAxis(X_AXIS);
        plot.setNoDataMessage(t.noData());
        return chart;
    }

    public String toSvgElement() {
        JFreeChart chart = createChart(createDataset());
        SVGGraphics2D g2 = new SVGGraphics2D(640, 480);
        Rectangle r = new Rectangle(0, 0, 640, 480);
        chart.draw(g2, r);
        return g2.getSVGElement();
    }
}
