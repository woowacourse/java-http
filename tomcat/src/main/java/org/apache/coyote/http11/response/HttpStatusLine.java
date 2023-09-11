package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpProtocol;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpStatusLine {
    private final HttpProtocol protocol;
    private final HttpStatus status;

    private HttpStatusLine(final HttpProtocol protocol, final HttpStatus status) {
        this.protocol = protocol;
        this.status = status;
    }

    public static HttpStatusLine empty() {
        return new HttpStatusLine(null, null);
    }

    public static HttpStatusLine of(final HttpProtocol protocol, final HttpStatus status) {
        return new HttpStatusLine(protocol, status);
    }

    public String message() {
        return protocol.message() + status.message();
    }
}
