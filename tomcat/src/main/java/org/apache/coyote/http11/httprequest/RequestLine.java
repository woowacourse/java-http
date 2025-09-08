package org.apache.coyote.http11.httprequest;

public class RequestLine {

    private static final String REQUEST_HEADER_SEPARATOR = " ";

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final String httpVersion;

    public static RequestLine from(final String rawRequestLine) {
        final String[] requestLineElements = rawRequestLine.split(REQUEST_HEADER_SEPARATOR);
        final HttpMethod httpMethod = HttpMethod.from(requestLineElements[0]);
        final RequestUri requestUri = RequestUri.from(requestLineElements[1]);
        final String httpVersion = requestLineElements[2];

        return new RequestLine(httpMethod, requestUri, httpVersion);
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public boolean isPathEqualsTo(final String requestPath) {
        return this.requestUri.isPathEqualsTo(requestPath);
    }

    public String getRequestPath() {
        return this.requestUri.getRequestPath();
    }

    private RequestLine(final HttpMethod httpMethod, final RequestUri requestUri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public boolean isMethodEqualsTo(final HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }
}
