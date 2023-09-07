package org.apache.coyote.http11.request;

public class HttpRequestStartLine {
    private static final String QUERY_DELIMITER = "?";

    private final HttpMethod httpMethod;
    private final String path;
    private final Query query;
    private final String version;

    private HttpRequestStartLine(final HttpMethod httpMethod, final String path, final Query query, final String version) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.query = query;
        this.version = version;
    }

    public static HttpRequestStartLine from(final String startLine) {
        final String[] startLines = startLine.split(" ");
        String path = startLines[1];
        final Query query = extractQuery(path);
        if (path.equals("/login") || path.equals("/register")) {
            path = path + ".html";
        }
        return new HttpRequestStartLine(HttpMethod.valueOf(startLines[0]), path, query, startLines[2]);
    }

    private static Query extractQuery(final String path) {
        if (isExistQuery(path)) {
            return Query.from(path.substring(path.indexOf(QUERY_DELIMITER) + 1));
        }
        return Query.empty();
    }

    private static boolean isExistQuery(final String path) {
        return path.indexOf(QUERY_DELIMITER) != -1;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Query getQuery() {
        return query;
    }

    public String getVersion() {
        return version;
    }
}
