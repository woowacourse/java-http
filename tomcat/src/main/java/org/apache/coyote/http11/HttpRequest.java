package org.apache.coyote.http11;

import java.net.URI;

final class HttpRequest {

    private final String method;
    private final URI uri;

    private HttpRequest(final String method, final URI uri) {
        this.method = method;
        this.uri = uri;
    }

    static HttpRequest from(final String requestLine) {
        final String[] requestLineParts = requestLine.split(" ", 3);
        return new HttpRequest(requestLineParts[0], URI.create(requestLineParts[1]));
    }

    String method() {
        return method;
    }

    String path() {
        return uri.getPath();
    }

    QueryParameters queryParameters() {
        return QueryParameters.from(uri.getRawQuery());
    }
}
