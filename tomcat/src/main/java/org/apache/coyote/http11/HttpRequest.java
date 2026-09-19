package org.apache.coyote.http11;

import java.net.URI;
import java.util.List;

final class HttpRequest {
    private final String method;
    private final URI uri;
    private final QueryParameters queryParameters;
    private final QueryParameters bodyParameters;

    private HttpRequest(final String method,
                        final URI uri,
                        final QueryParameters queryParameters,
                        final QueryParameters bodyParameters) {
        this.method = method;
        this.uri = uri;
        this.queryParameters = queryParameters;
        this.bodyParameters = bodyParameters;
    }

    static HttpRequest from(final List<String> requestLines) {
        return of(requestLines,null);
    }

    static HttpRequest of(final List<String> requestLines, String body) {
        final String[] requestLineParts = requestLines.getFirst().split(" ", 3);
        final URI uri = URI.create(requestLineParts[1]);
        final QueryParameters queryParameters = QueryParameters.from(uri.getRawQuery());

        QueryParameters bodyParameters = QueryParameters.from(body);

        return new HttpRequest(requestLineParts[0], uri, queryParameters, bodyParameters);
    }


    String getMethod() {
        return method;
    }

    String getPath() {
        return uri.getPath();
    }

    String getParameter(final String name) {
        return queryParameters.get(name).orElse(null);
    }

    String getBodyParameter(final String name) {
        return bodyParameters.get(name).orElse(null);
    }
}
