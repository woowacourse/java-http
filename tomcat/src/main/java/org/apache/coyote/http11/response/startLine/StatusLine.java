package org.apache.coyote.http11.response.startLine;

public class StatusLine {

    private final String protocol;
    private final HttpStatus httpStatus;

    public StatusLine(String protocol, HttpStatus httpStatus) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
    }

    public String compose() {
        return protocol + " " + httpStatus.compose();
    }
}
