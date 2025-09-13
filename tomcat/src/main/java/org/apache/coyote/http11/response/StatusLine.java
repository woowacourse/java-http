package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class StatusLine {

    private final HttpStatus httpStatus;

    public StatusLine(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String convertResponseStatusLineMessage() {
        return String.format(
                "%s %d %s\r\n",
                "HTTP/1.1",
                httpStatus.getStatusCode(),
                httpStatus.getReasonPhrase()
        );
    }
}
