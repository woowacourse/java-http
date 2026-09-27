package org.apache.coyote.response;

public class StatusLine {
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final String CRLF = "\r\n";

    private final String httpVersion;
    private final StatusCode statusCode;

    public static StatusLine build(StatusCode statusCode) {
        return new StatusLine(HTTP_VERSION, statusCode);
    }

    private StatusLine(String httpVersion, StatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return httpVersion + " " + statusCode.getCode() + " " + statusCode.getReasonPhrase() + CRLF;
    }
}
