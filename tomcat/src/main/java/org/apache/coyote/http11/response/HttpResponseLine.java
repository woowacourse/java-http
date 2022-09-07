package org.apache.coyote.http11.response;

public class HttpResponseLine {

    private static final String VERSION = "HTTP/1.1";

    private final String version;
    private final int code;
    private final String message;

    public HttpResponseLine(final String version, final int code, final String message) {
        this.version = version;
        this.code = code;
        this.message = message;
    }

    public HttpResponseLine(final HttpStatus httpStatus) {
        this(VERSION, httpStatus.getCode(), httpStatus.getMessage());
    }

    @Override
    public String toString() {
        return version + " " + code + " " + message;
    }
}
