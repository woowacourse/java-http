package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String protocolVersion;
    private final HttpStatus status;

    public StatusLine(String protocolVersion, HttpStatus status) {
        this.protocolVersion = protocolVersion;
        this.status = status;
    }

    public StatusLine(HttpStatus status) {
        this(HTTP_VERSION, status);
    }

    public String formatStatusLine() {
        return String.format("%s %s %s ", protocolVersion, status.getCode(), status.getReasonPhrase());
    }
}
