package org.openhab.binding.electric.internal.handler.price;

import static org.junit.jupiter.api.Assertions.fail;
import static org.openhab.binding.electric.internal.handler.entsoe.EntsoeClientTest.CZ_FILE_2015;
import static org.openhab.binding.electric.internal.handler.entsoe.EntsoeClientTest.FI_FILE_2022;
import static org.openhab.binding.electric.internal.handler.entsoe.EntsoeClientTest.FI_FILE_2023;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jfree.svg.SVGUtils;
import org.junit.jupiter.api.Test;
import org.openhab.binding.electric.common.AbstractTest;
import org.openhab.binding.electric.internal.handler.price.service.PriceCache;
import org.openhab.binding.electric.internal.handler.price.service.PriceGraph;

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
