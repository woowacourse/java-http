package org.apache.coyote.http11.Request;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private final Method method;
    private final String uri;
    private final String version;

    public RequestLine(final String requestLine) {
        final String[] values = requestLine.split(REQUEST_LINE_DELIMITER);
        this.method = Method.from(values[0]);
        this.uri = values[1];
        this.version = values[2];
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return uri.split("\\?")[0];
    }
}
