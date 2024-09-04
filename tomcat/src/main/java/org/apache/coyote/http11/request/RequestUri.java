package org.apache.coyote.http11.request;

public class RequestUri {

    private static final String QUERY_START = "?";

    private final String path;
    private final Queries queries;

    public RequestUri(String path, Queries queries) {
        this.path = path;
        this.queries = queries;
    }

    public static RequestUri of(String uri) {
        boolean hasQueries = hasQueries(uri);
        if (!hasQueries) {
            return new RequestUri(uri, Queries.EMPTY_QUERIES);
        }
        int queryStartIndex = uri.indexOf(QUERY_START);
        String path = uri.substring(0, queryStartIndex);
        String queryString = uri.substring(queryStartIndex + 1);
        return new RequestUri(path, Queries.of(queryString));
    }

    private static boolean hasQueries(String uri) {
        int queryStartIndex = uri.indexOf(QUERY_START);
        return queryStartIndex != -1 && queryStartIndex != uri.length() - 1;
    }

    public String getPath() {
        return path;
    }

    public Queries getQueries() {
        return queries;
    }
}
