package org.apache.coyote.http11.Request;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private String method;
    private String uri;
    private String version;

    private RequestLine() {
    }

    public RequestLine(final String requestLine) {
        final String[] values = requestLine.split(REQUEST_LINE_DELIMITER);
        this.method = values[0];
        this.uri = values[1];
        this.version = values[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return uri.split("\\?")[0];
    }
}
