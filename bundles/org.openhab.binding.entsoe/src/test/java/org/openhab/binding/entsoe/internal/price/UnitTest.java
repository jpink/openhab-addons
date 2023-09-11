package org.openhab.binding.entsoe.internal.price;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openhab.core.library.unit.Units;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class UnitTest {

    @Test
    public void euroMWh_to_ckWh() {
        var foo = Units.KILOWATT_HOUR;
        var bar = Units.MEGAWATT_HOUR;
        var converter = bar.getConverterTo(foo);
        System.out.println(converter);

        assertEquals("12", converter.convert(12));
    }

    @Test
    public void Zones() {
        var foo = ZonedDateTime.now();
        System.out.println(foo);
        foo = foo.withZoneSameInstant(ZoneOffset.UTC);
        System.out.println(foo);
    }

}
