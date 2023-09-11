package org.openhab.binding.entsoe.internal.price.service;

import java.io.Serial;

/** Unexpected exception which means bug in price service. */
public class Bug extends Exception {
    @Serial
    private static final long serialVersionUID = 6476362399567760465L;

    public Bug(Throwable cause) {
        super(cause);
    }

}
