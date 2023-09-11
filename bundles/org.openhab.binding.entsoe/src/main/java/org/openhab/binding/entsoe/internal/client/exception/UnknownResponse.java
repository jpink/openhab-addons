package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;

public class UnknownResponse extends Exception {

    @Serial
    private static final long serialVersionUID = -3279306525482845066L;
    public final String url;
    public final int status;

    public UnknownResponse(String url, int status, String content, Exception cause) {
        super(content, cause);
        this.status = status;
        this.url = url;
    }

}
