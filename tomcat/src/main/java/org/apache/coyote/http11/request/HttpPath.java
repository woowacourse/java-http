package org.apache.coyote.http11.request;

public class HttpPath {
    private static final String QUERY_DELIMITER = "?";

    private final String path;
    private final HttpQuery query;

    private HttpPath(final String path, final HttpQuery query) {
        this.path = path;
        this.query = query;
    }

    public static HttpPath from(final String path) {
        if (isExistQuery(path)) {
            final HttpQuery query = HttpQuery.from(path.substring(path.indexOf(QUERY_DELIMITER)));
            return new HttpPath(path, query);
        }
        return new HttpPath(path, HttpQuery.empty());
    }

    private static boolean isExistQuery(final String path) {
        return path.contains(QUERY_DELIMITER);
    }

    public String getPath() {
        return path;
    }
}
