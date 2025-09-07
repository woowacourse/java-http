package org.apache.coyote.http11.response;

public class StatusLine { // HTTP/1.1 200 OK

    private final String protocol; // HTTP/1.1
    private final HttpStatus httpStatus; // 200 OK

    public StatusLine() {
        this.protocol = "HTTP/1.1";
        this.httpStatus = HttpStatus.OK;
    }

    public StatusLine(HttpStatus httpStatus) {
        this.protocol = "HTTP/1.1";
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return String.join(" ",
                protocol,
                httpStatus.toString());
    }
}
