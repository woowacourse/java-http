package org.apache.coyote.http11;

public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatus httpStatus;

    public StatusLine(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getStatusLine() {
        return String.join(" ", HTTP_VERSION, httpStatus.getStatusCode(), httpStatus.getText());
    }
}
