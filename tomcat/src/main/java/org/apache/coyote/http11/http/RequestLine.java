package org.apache.coyote.http11.http;

import org.apache.coyote.http11.util.HttpMethod;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;

    private RequestLine(final HttpMethod httpMethod, final RequestUri requestURI) {
        this.httpMethod = httpMethod;
        this.requestUri = requestURI;
    }

    public static RequestLine from(final String line) {
        final String[] lines = line.split(" ");
        final var httpMethod = HttpMethod.from(lines[0]);
        final var requestURI = RequestUri.from(lines[1]);

        return new RequestLine(httpMethod, requestURI);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public boolean containUrl(final String url) {
        return requestUri.containUrl(url);
    }
}
