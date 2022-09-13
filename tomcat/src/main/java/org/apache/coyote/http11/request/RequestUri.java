package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class RequestUri {
    private static final String QUERY_PARAMETER_SIGN = "?";

    private final String path;
    private final Map<String, String> params;

    private RequestUri(final String path, final Map<String, String> params) {
        this.path = path;
        this.params = params;
    }

    public static RequestUri from(final String uri) {
        if (uri.contains(QUERY_PARAMETER_SIGN)) {
            final int index = uri.indexOf(QUERY_PARAMETER_SIGN);
            final String path = uri.substring(0, index);

            return new RequestUri(path, parseQueryParams(uri, index));
        }

        return new RequestUri(uri, Collections.emptyMap());
    }

    private static Map<String, String> parseQueryParams(final String uri, final int index) {
        final String queryString = uri.substring(index + 1);
        return QueryParser.parse(queryString, "올바른 Query Parameter 형식이 아닙니다.");
    }

    public String findQueryValue(final String name) {
        return Optional.ofNullable(params.get(name))
                .orElseThrow(() -> new NoSuchElementException("Query Parameter에 key 값이 존재하지 않습니다."));
    }

    public boolean containsQuery() {
        return !params.isEmpty();
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
