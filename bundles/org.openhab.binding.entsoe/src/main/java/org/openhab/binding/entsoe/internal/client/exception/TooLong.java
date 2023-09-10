package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;
import java.time.Duration;

public class TooLong extends Exception {
    @Serial
    private static final long serialVersionUID = 7232286219011534478L;
    public final Duration max;
    public final Duration value;

    public TooLong(Duration value, Duration max) {
        super(value + " > " + max);
        this.max = max;
        this.value = value;
    }
}
