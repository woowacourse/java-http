package org.apache.coyote.http11.request.requestline;

import org.apache.coyote.http11.request.QueryParameters;

import java.util.Optional;

public class RequestUri {

    private static final String QUERY_DELIMITER = "?";
    private static final int NOT_FOUND = -1;

    private final RequestPath path;
    private final QueryParameters queryParameters;

    public RequestUri(final RequestPath path, final QueryParameters queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static RequestUri from(final String uri) {
        final int queryIndex = uri.indexOf(QUERY_DELIMITER);
        final boolean hasQuery = queryIndex != NOT_FOUND;

        final String rawPath = hasQuery ? uri.substring(0, queryIndex) : uri;
        final String queryString = hasQuery ? uri.substring(queryIndex + 1) : null;

        return new RequestUri(RequestPath.from(rawPath), QueryParameters.from(queryString));
    }

    public boolean hasQueryParameters() {
        return !queryParameters.isEmpty();
    }

    public RequestPath getRequestPath() {
        return path;
    }

    public Optional<String> getQueryParameter(final String name) {
        return queryParameters.get(name);
    }
}