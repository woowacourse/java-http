package org.apache.coyote.http11.request;

import org.apache.coyote.exception.HttpParseException;

import java.util.Optional;

public class RequestUri {
    private static final String URI_PREFIX = "/";
    private static final String QUERY_DELIMITER = "\\?";

    private final String path;
    private final QueryParameters queryParameters;

    public RequestUri(String uri) {
        validateUri(uri);
        String[] uriParts = uri.split(QUERY_DELIMITER, 2);
        this.path = uriParts[0];
        if (uriParts.length == 1) {
            this.queryParameters = QueryParameters.empty();
            return;
        }
        this.queryParameters = QueryParameters.parse(uriParts[1]);
    }

    private void validateUri(String uri) {
        if (!uri.startsWith(URI_PREFIX)) {
            throw new HttpParseException("요청 경로는 '/'로 시작해야 합니다: " + uri);
        }
    }

    public String getPath() {
        return path;
    }

    public Optional<String> findParameter(String key) {
        return queryParameters.find(key);
    }
}
