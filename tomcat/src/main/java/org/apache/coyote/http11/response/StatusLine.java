package org.apache.coyote.http11.response;

public class StatusLine {
    private final String statusLine;

    private StatusLine(final String statusLine) {
        this.statusLine = statusLine;
    }

    public static StatusLine from(final HttpStatus httpStatus) {
        return new StatusLine("HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.name() + " ");
    }

    public String getStatusLine() {
        return statusLine;
    }
}
