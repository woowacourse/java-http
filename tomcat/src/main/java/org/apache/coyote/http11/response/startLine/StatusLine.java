package org.apache.coyote.http11.response.startLine;

public class StatusLine {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String toMessage() {
        return PROTOCOL_VERSION + " " + httpStatus.toMessage();
    }
}
