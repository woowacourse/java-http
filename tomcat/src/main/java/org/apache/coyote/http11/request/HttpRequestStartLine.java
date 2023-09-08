package org.apache.coyote.http11.request;

public class HttpRequestStartLine {
    private static final String QUERY_DELIMITER = "?";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;

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
        String path = startLines[PATH_INDEX];
        final Query query = extractQuery(path);
        if (path.equals("/login") || path.equals("/register")) {
            path = path + ".html";
        }
        return new HttpRequestStartLine(HttpMethod.valueOf(startLines[METHOD_INDEX]), path, query, startLines[VERSION_INDEX]);
    }

    private static Query extractQuery(final String path) {
        if (isExistQuery(path)) {
            return Query.from(path.substring(path.indexOf(QUERY_DELIMITER) + PATH_INDEX));
        }
        return Query.empty();
    }

    private static boolean isExistQuery(final String path) {
        return path.contains(QUERY_DELIMITER);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }
}
