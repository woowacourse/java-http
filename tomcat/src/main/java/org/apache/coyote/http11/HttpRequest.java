package org.apache.coyote.http11;

import java.net.URI;

final class HttpRequest {

    private final String method;
    private final URI uri;
    private final QueryParameters queryParameters;

    private HttpRequest(final String method, final URI uri, final QueryParameters queryParameters) {
        this.method = method;
        this.uri = uri;
        this.queryParameters = queryParameters;
    }

    static HttpRequest from(final String requestLine) {
        final String[] requestLineParts = requestLine.split(" ", 3);
        final URI uri = URI.create(requestLineParts[1]);
        final QueryParameters queryParameters = QueryParameters.from(uri.getRawQuery());
        return new HttpRequest(requestLineParts[0], uri, queryParameters);
    }

    String method() {
        return method;
    }

    String path() {
        return uri.getPath();
    }

    String getParameter(final String name) {
        return queryParameters.get(name).orElse(null);
    }

    boolean hasParameters() {
        return !queryParameters.isEmpty();
    }
}
