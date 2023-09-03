package org.apache.coyote.http11.request;

public class RequestLine {

    private static final String BLANK = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final String method;
    private final String uri;
    private final String httpVersion;

    private RequestLine(final String method, final String uri, final String httpVersion) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(final String requestLine) {
        final String[] splitRequestLine = requestLine.split(BLANK);

        final String method = splitRequestLine[HTTP_METHOD_INDEX];
        final String uri = splitRequestLine[HTTP_REQUEST_URI_INDEX];
        final String httpVersion = splitRequestLine[HTTP_VERSION_INDEX];

        return new RequestLine(method, uri, httpVersion);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
