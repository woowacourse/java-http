package org.apache.coyote.http11.response;

public class ResponseLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final String version;
    private final int code;
    private final String message;

    public ResponseLine(String version, int code, String message) {
        this.version = version;
        this.code = code;
        this.message = message;
    }

    public ResponseLine(final HttpStatus httpStatus) {
        this(HTTP_VERSION, httpStatus.getCode(), httpStatus.getMessage());
    }

    @Override
    public String toString() {
        return version + " " + code + " " + message;
    }
}
