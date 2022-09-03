package org.apache.coyote.http11.request;

public class RequestLine {
    private static final int DEFAULT_LENGTH = 3;
    private final String method;
    private final RequestUri requestUri;
    private final String version;

    private RequestLine(final String method, final RequestUri requestUri, final String version) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
    }

    public static RequestLine of(final String line) {
        final String[] requestLine = line.split(" ");
        validateLength(requestLine);

        final String method = requestLine[0];
        final RequestUri requestUri = RequestUri.from(requestLine[1]);
        final String version = requestLine[2];

        return new RequestLine(method, requestUri, version);
    }

    private static void validateLength(final String[] requestLine) {
        if (requestLine.length != DEFAULT_LENGTH) {
            throw new IllegalArgumentException("invalid request line length");
        }
    }

    public String getMethod() {
        return method;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public String getVersion() {
        return version;
    }
}
