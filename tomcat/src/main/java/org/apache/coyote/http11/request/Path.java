package org.apache.coyote.http11.request;

public class Path {

    private static final int URI_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String URI_SEPARATOR = "\\?";

    private final String uri;
    private final QueryString queryString;

    private Path(final String uri, final QueryString queryString) {
        this.uri = uri;
        this.queryString = queryString;
    }

    public static Path from(String request) {
        final String[] uri = parseUri(request);
        final QueryString queryString = convertFrom(uri[QUERY_STRING_INDEX]);

        return new Path(uri[URI_INDEX], queryString);
    }

    private static String[] parseUri(final String uri) {
        if (uri.contains(QUERY_STRING_SEPARATOR)) {
            return uri.split(URI_SEPARATOR);
        }

        return new String[]{uri, null};
    }

    private static QueryString convertFrom(String queryString) {
        if (queryString == null) {
            return null;
        }

        return QueryString.from(queryString);
    }

    public boolean isSameUri(final String uri) {
        return this.uri.equals(uri);
    }

    public String getUri() {
        return uri;
    }

    public QueryString getQueryString() {
        return queryString;
    }
}
