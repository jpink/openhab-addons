package org.openhab.binding.entsoe.internal.client.exception;

import java.io.Serial;

public class UnknownResponse extends Exception {
    @Serial
    private static final long serialVersionUID = -7825475193697097663L;
    public final String url;
    public final int status;

    public UnknownResponse(String url, int status, String content) {
        super(content);
        this.status = status;
        this.url = url;
    }

}
