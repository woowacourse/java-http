package org.apache.coyote.http11.http;

import org.apache.coyote.http11.util.HttpMethod;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final RequestURI requestURI;

    private RequestLine(final HttpMethod httpMethod, final RequestURI requestURI) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
    }

    public static RequestLine from(final String line) {
        final String[] lines = line.split(" ");
        final var httpMethod = HttpMethod.from(lines[0]);
        final var requestURI = RequestURI.from(lines[1]);

        return new RequestLine(httpMethod, requestURI);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }
}
