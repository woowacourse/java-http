package org.apache.coyote.http11.httpResponse;

import org.apache.coyote.http11.HttpStatus;

public class StatusLine {

    private final String protocolVersion;
    private final HttpStatus httpStatus;

    private StatusLine(
            final String protocolVersion,
            final HttpStatus httpStatus
    ) {
        this.protocolVersion = protocolVersion;
        this.httpStatus = httpStatus;
    }

    public static StatusLine build(final HttpStatus httpStatus) {
        return new StatusLine("HTTP/1.1", httpStatus);
    }

    @Override
    public String toString() {
        return protocolVersion + " " + httpStatus.toString();
    }
}
