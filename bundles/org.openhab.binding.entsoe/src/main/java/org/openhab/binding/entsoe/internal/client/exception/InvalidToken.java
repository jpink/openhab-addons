package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;

public class InvalidToken extends Exception {
    @Serial
    private static final long serialVersionUID = 3456308814200380427L;

    public InvalidToken(IllegalArgumentException cause) {
        super(cause);
    }

}