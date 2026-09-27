package org.apache.coyote.http11;

public class StatusLine {
    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private final HttpStatus httpStatus;

    public StatusLine(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String format() {
        return PROTOCOL_VERSION + " " + httpStatus.getCode() + " " + httpStatus.getMessage();
    }
}
