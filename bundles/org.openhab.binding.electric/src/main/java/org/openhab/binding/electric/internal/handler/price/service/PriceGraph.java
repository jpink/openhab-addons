/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.electric.internal.handler.price.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.text.G2TextMeasurer;
import org.jfree.chart.text.TextBlock;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.svg.SVGGraphics2D;
import org.openhab.binding.electric.internal.handler.Translations;
import org.openhab.binding.electric.internal.handler.price.PriceConfig;

/**
 * Price graph.
 *
 * @author Jukka Papinkivi - Initial contribution
 */
@NonNullByDefault
public class PriceGraph {
    private static class ZonedDateTimeAxis extends CategoryAxis {
        @Serial
        private static final long serialVersionUID = -7835789623702120751L;

        @Override
        protected TextBlock createLabel(@Nullable Comparable category, float width, @Nullable RectangleEdge edge,
                @Nullable Graphics2D g2) {
            return TextUtils.createTextBlock(
                    (category instanceof ZonedDateTime time) ? Integer.toString(time.getHour()) : "?",
                    getTickLabelFont(category), getTickLabelPaint(category), width, 1, new G2TextMeasurer(g2));
        }
    }

    private static final Color // For bar series
    BLUE = new Color(38, 49, 109), // Like letters in the ENTSO-E logo.
            GREEN = new Color(0, 101, 46), // Like in the Finnish Tax Administration logo.
            YELLOW = new Color(252, 196, 15), // Like in ENTSO-E logo.
            RED = new Color(213, 18, 30), // Like Fingrid logo.
            TEAL = new Color(26, 116, 131); // Like the homepage footer of the Ministry of Finance in Finland.

    private static final StackedBarRenderer RENDERER = new StackedBarRenderer();
    private static final CategoryAxis X_AXIS = new ZonedDateTimeAxis();

    static {
        RENDERER.setSeriesPaint(0, GREEN); // VAT
        RENDERER.setSeriesPaint(1, RED); // transfer
        RENDERER.setSeriesPaint(2, BLUE); // spot
        RENDERER.setSeriesPaint(3, YELLOW); // margin
        RENDERER.setSeriesPaint(4, TEAL); // tax

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
                t.electricityPrice() + " " + dateFormat.format(cache.start.toLocalDate()), null,
                config.showInCents() ? t.centPerKilowattHour() : config.targetUnit.toString(), dataset,
                PlotOrientation.VERTICAL, true, false, false);
        var plot = chart.getCategoryPlot();
        plot.setDomainAxis(X_AXIS);
        plot.setRenderer(RENDERER);
        plot.setNoDataMessage(t.noData());
        return chart;
    }

    public String toSvgElement() {
        JFreeChart chart = createChart(createDataset());
        SVGGraphics2D g2 = new SVGGraphics2D(640, 480);
        chart.draw(g2, new Rectangle(0, 0, 640, 480));
        return g2.getSVGElement();
    }
}
