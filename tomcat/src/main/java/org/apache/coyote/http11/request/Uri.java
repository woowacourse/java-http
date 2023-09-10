package org.apache.coyote.http11.request;

public class Uri {

    private static final String QUERY_STRING_START_LETTER = "?";
    private static final String EMPTY_STRING = "";

    private final String path;
    private final QueryString queryString;

    public static Uri from(final String uri) {
        if (uri.contains(QUERY_STRING_START_LETTER)) {
            final int index = uri.indexOf(QUERY_STRING_START_LETTER);
            final String path = uri.substring(0, index);
            final String queryString = uri.substring(index + 1);
            return new Uri(path, queryString);
        }
        return new Uri(uri, EMPTY_STRING);
    }

    private Uri(final String path, final String queryString) {
        this.path = path;
        this.queryString = QueryString.from(queryString);
    }

    public String getPath() {
        return path;
    }
}
