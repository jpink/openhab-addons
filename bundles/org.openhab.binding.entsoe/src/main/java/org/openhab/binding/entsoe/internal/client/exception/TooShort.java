package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;
import java.time.Duration;

public class TooShort extends Exception {
    @Serial
    private static final long serialVersionUID = 4818451922083980812L;
    public final Duration min;
    public final Duration value;

    public TooShort(Duration value, Duration min) {
        super(value + " < " + min);
        this.min = min;
        this.value = value;
    }

}
