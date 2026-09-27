package org.apache.coyote.http11;

public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatus status;

    public StatusLine(final HttpStatus status) {
        this.status = status;
    }

    public String format() {
        return String.join(" ", HTTP_VERSION, String.valueOf(status.getCode()), status.getReasonPhrase()) + " ";
    }
}
