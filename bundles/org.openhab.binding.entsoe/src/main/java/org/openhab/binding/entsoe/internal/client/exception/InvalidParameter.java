package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;
import java.net.MalformedURLException;
import java.net.URL;

public class InvalidParameter extends Exception {
    @Serial
    private static final long serialVersionUID = -1662688835270360171L;

    private static String getQuery(String url) {
        try {
            return new URL(url).getQuery();
        } catch (MalformedURLException e) {
            return e.getMessage();
        }
    }

    public InvalidParameter(String url) {
        super(getQuery(url));
    }

}
