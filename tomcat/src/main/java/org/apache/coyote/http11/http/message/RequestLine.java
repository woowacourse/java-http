package org.apache.coyote.http11.http.message;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private final String method;
    private final String uri;
    private final String version;


    private RequestLine(final String method, final String uri, final String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static RequestLine from(final String requestLine) {
        final String[] requestLines = requestLine.split(REQUEST_LINE_DELIMITER);
        final String method = requestLines[0];
        final String uri = requestLines[1];
        final String version = requestLines[2];
        return new RequestLine(method, uri, version);
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }
}
