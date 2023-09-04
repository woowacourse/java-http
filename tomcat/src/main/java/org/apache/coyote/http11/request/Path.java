package org.apache.coyote.http11.request;

public class Path {

    private static final String QUERY_STRING_BEGIN = "?";
    private static final int EMPTY_QUERY_STRING = -1;

    private final String value;

    private Path(final String value) {
        this.value = value;
    }

    public static Path from(final String path) {
        return new Path(path);
    }

    public String parseUri() {
        final int queryStringIndex = value.indexOf(QUERY_STRING_BEGIN);
        if (queryStringIndex == EMPTY_QUERY_STRING) {
            return value;
        }
        return value.substring(0, queryStringIndex);
    }

    public QueryString parseQueryString() {
        return QueryString.from(value);
    }
}
