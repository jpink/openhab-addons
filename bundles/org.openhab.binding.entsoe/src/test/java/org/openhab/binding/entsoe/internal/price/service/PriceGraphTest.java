package org.openhab.binding.entsoe.internal.price.service;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jfree.svg.SVGUtils;
import org.junit.jupiter.api.Test;
import org.openhab.binding.entsoe.internal.common.AbstractTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.CZ_FILE_2015;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.FI_FILE_2022;
import static org.openhab.binding.entsoe.internal.client.EntsoeClientTest.FI_FILE_2023;

@NonNullByDefault
class PriceGraphTest extends AbstractTest {
    void write(PriceCache cache, String name) {
        var graph = new PriceGraph(cache, translations());
        var file = generatedResourceFile(name + ".svg");
        try {
            SVGUtils.writeToSVG(file, graph.toSvgElement());
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void writeCz2015() {
        write(new PriceCacheTest().createCz2015(), CZ_FILE_2015);
    }

    @Test
    void writeFi2022() {
        write(new PriceCacheTest().createFi2022(), FI_FILE_2022);
    }

    @Test
    void writeFi2023() {
        write(new PriceCacheTest().createFi2023(), FI_FILE_2023);
    }
}