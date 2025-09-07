package org.apache.coyote.http11.httpRequest;

public class HttpRequestHeaders {

    private final HttpMethod method;
    private final String path;
    private final QueryStrings queryStrings;
    private final int contentLength;

    public HttpRequestHeaders(HttpMethod method, String path, QueryStrings queryStrings, int contentLength) {
        this.method = method;
        this.path = path;
        this.queryStrings = queryStrings;
        this.contentLength = contentLength;
    }

    public boolean pathEquals(String path) {
        return this.path.equals(path);
    }

    public boolean isQueryStringsEmpty() {
        return this.queryStrings.isEmpty();
    }

    public String getQueryStringOf(String key) {
        return this.queryStrings.get(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return this.path;
    }

    public int getContentLength() {
        return this.contentLength;
    }
}
