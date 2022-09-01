package org.apache.coyote.http11.response;

public class HttpResponseLine {

    private final String protocol;
    private final int code;
    private final String message;

    public HttpResponseLine(final String protocol, final int code, final String message) {
        this.protocol = protocol;
        this.code = code;
        this.message = message;
    }

    public HttpResponseLine(final HttpStatus httpStatus) {
        this("HTTP/1.1", httpStatus.getCode(), httpStatus.getMessage());
    }

    @Override
    public String toString() {
        return protocol + " " + code + " " + message;
    }
}
