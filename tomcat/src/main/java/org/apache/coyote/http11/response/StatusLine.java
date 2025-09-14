package org.apache.coyote.http11.response;

public class StatusLine { // HTTP/1.1 200 OK

    private static final String PROTOCOL_HTTP11 = "HTTP/1.1";

    private final String protocol; // HTTP/1.1
    private HttpStatus httpStatus; // 200 OK

    public StatusLine() {
        this.protocol = PROTOCOL_HTTP11;
        this.httpStatus = HttpStatus.NONE;
    }

    public StatusLine(HttpStatus httpStatus) {
        this.protocol = PROTOCOL_HTTP11;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return String.join(" ",
                protocol,
                httpStatus.toString());
    }
}
