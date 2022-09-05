package org.apache.coyote.http11;

import java.util.Map;
import org.apache.coyote.http11.util.QueryStringResolver;

public class Request {
    private static final String QUERY_PARAM_PREFIX = "?";
    private static final int NEXT_INDEX = 1;

    private final Map<String, String> queryParams;
    private final String requestURI;

    private Request(final String requestURI) {
        final var queryString = parseQueryString(requestURI);
        this.requestURI = requestURI;
        this.queryParams = QueryStringResolver.resolve(queryString);
    }

    private static String parseQueryString(final String requestURI) {
        int index = requestURI.indexOf(QUERY_PARAM_PREFIX);
        return requestURI.substring(index + NEXT_INDEX);
    }

    public static Request from(final String requestURI) {
        return new Request(requestURI);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
