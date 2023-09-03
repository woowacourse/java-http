package org.apache.coyote.http11.response;

public class StatusLine {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private final HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String httpVersion() {
        return HTTP_VERSION;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return String.join(" ",
                HTTP_VERSION,
                String.valueOf(httpStatus.statusCode()),
                httpStatus.responsePhrase()
        ) + " \r\n";
    }
}
