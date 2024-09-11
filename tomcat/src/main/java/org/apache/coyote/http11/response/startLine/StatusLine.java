package org.apache.coyote.http11.response.startLine;

import static org.apache.coyote.http11.Constants.STATUS_LINE_DELIMITER;

public class StatusLine {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String toMessage() {
        return PROTOCOL_VERSION + STATUS_LINE_DELIMITER + httpStatus.toMessage();
    }
}
