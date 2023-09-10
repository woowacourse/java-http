package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatusCode httpStatusCode;

    public StatusLine(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String toString() {
        return HTTP_VERSION + " " + httpStatusCode.getStatusCode() + " " + httpStatusCode.name();
    }
}
