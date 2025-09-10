package org.apache.coyote.http11.request.startline;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final String httpVersion;

    public static RequestLine from(final String rawRequestLine) {
        final RequestLineParser parser = RequestLineParser.getInstance();

        final HttpMethod httpMethod = parser.parseMethod(rawRequestLine);
        final RequestUri requestUri = parser.parseUri(rawRequestLine);
        final String httpVersion = parser.parseVersion(rawRequestLine);

        return new RequestLine(httpMethod, requestUri, httpVersion);
    }

    public boolean isPathEqualsTo(final String requestPath) {
        return this.requestUri.isPathEqualsTo(requestPath);
    }

    public String getRequestPath() {
        return this.requestUri.getRequestPath();
    }

    public boolean isMethodEqualsTo(final HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    private RequestLine(final HttpMethod httpMethod, final RequestUri requestUri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }
}
