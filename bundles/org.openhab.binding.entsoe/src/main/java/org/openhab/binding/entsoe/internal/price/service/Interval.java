package org.openhab.binding.entsoe.internal.price.service;

import java.time.ZonedDateTime;

public interface Interval {
    ZonedDateTime start();

    ZonedDateTime end();

    default boolean contains(ZonedDateTime that) {
        // Times are usually iterated in chronological order so end test fails faster.
        // End is exclusive and start is inclusive.
        return that.compareTo(end()) < 0 && start().compareTo(that) <= 0;
    }

}
