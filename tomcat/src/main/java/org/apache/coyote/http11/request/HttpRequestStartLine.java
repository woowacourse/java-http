package org.apache.coyote.http11.request;

public class HttpRequestStartLine {
    private final HttpMethod httpMethod;
    private final String path;
    private final String query;
    private final String httpVersion;

    private HttpRequestStartLine(final HttpMethod httpMethod, final String path, final String query, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.query = query;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLine from(String startLine) {
        final String[] splitLine = startLine.split(" ");
        String path = splitLine[1];
        String query = "";
        if (path.indexOf("?") != -1) {
            query = path.substring(path.indexOf("?") + 1);
        }
        if (path.equals("/login") || path.equals("/register")) {
            path = path + ".html";
        }
        return new HttpRequestStartLine(HttpMethod.valueOf(splitLine[0]), path, query, splitLine[2]);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
