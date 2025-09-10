package org.apache.coyote.http11.httpRequest;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final QueryStrings queryStrings;

    public RequestLine(HttpMethod method, String path, QueryStrings queryStrings) {
        this.method = method;
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public boolean pathEquals(String path) {
        return this.path.equals(path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return this.path;
    }
}
