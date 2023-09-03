package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestPath {
    private static final String QUERY_STRING_BEGGINER = "?";
    private static final int UN_EXISTED_INDEX = -1;
    private static final int NEXT = 1;
    private final String path;
    private final QueryString queryString;

    private RequestPath(final String path, final QueryString queryString) {
        this.path = path;
        this.queryString = queryString;
    }

    private RequestPath(final String path) {
        this(path, QueryString.EMPTY);
    }


    public static RequestPath from(final String path) {
        final int queryStringDelimiterIndex = path.indexOf(QUERY_STRING_BEGGINER);
        if (queryStringDelimiterIndex == UN_EXISTED_INDEX) {
            return new RequestPath(path);
        }

        return new RequestPath(
                path.substring(0, queryStringDelimiterIndex),
                QueryString.from(path.substring(queryStringDelimiterIndex + NEXT)));
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString.getQueryStrings();
    }

    public boolean hasQueryString() {
        return !queryString.equals(QueryString.EMPTY);
    }
}
