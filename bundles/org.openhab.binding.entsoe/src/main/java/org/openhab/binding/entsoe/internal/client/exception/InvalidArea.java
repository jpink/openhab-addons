package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;

public class InvalidArea extends Exception {
    @Serial
    private static final long serialVersionUID = -745040893268448199L;

    public InvalidArea(String area) {
        super(area);
    }

}
