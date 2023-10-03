package org.openhab.binding.electric.common;

import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;

public interface Interval {
    ZonedDateTime start();

    default boolean contains(ZonedDateTime time) {
        return time.isBefore(end()) && !time.isBefore(start());
    }

    ZonedDateTime end();

    default Duration duration() {
        return Duration.between(start(), end());
    }

    default Period period() {
        var end = end();
        var endDate = end.toLocalDate();
        if (end.toLocalTime().isAfter(LocalTime.MIN)) {
            endDate = endDate.plusDays(1);
        }
        return Period.between(start().toLocalDate(), endDate);
    }
}
