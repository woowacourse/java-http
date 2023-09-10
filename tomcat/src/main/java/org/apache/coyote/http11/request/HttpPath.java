package org.apache.coyote.http11.request;

public class HttpPath {
    private static final String QUERY_DELIMITER = "?";

    private final String path;

    private HttpPath(final String path) {
        this.path = path;
    }

    public static HttpPath from(final String path) {
        final HttpQuery query = extractQuery(path);
        if (path.equals("/login") || path.equals("/register")) {
            return new HttpPath(path + ".html");
        }
        return new HttpPath(path);
    }

    private static HttpQuery extractQuery(final String path) {
        if (isExistQuery(path)) {
            return HttpQuery.from(path.substring(path.indexOf(QUERY_DELIMITER)));
        }
        return HttpQuery.empty();
    }

    private static boolean isExistQuery(final String path) {
        return path.contains(QUERY_DELIMITER);
    }

    public String getPath() {
        return path;
    }
}
