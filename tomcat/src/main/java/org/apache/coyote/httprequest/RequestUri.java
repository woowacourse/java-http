package org.apache.coyote.httprequest;

public class RequestUri {

    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final int INVALID_INDEX = -1;
    private static final int START_INDEX = 0;
    private static final int NEXT_INDEX = 1;

    private final String path;
    private final QueryString queryString;

    private RequestUri(final String path, final QueryString queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    public static RequestUri from(final String requestUri) {
        final int queryStringSeparatorIndex = requestUri.indexOf(QUERY_STRING_SEPARATOR);
        if (queryStringSeparatorIndex == INVALID_INDEX) {
            return new RequestUri(requestUri, QueryString.empty());
        }
        final String path = requestUri.substring(START_INDEX, queryStringSeparatorIndex);
        return new RequestUri(path, QueryString.from(requestUri.substring(queryStringSeparatorIndex + NEXT_INDEX)));
    }

    public String getPath() {
        return path;
    }

    public QueryString getQueryString() {
        return queryString;
    }
}
