package org.apache.coyote.http11.request;

public class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;
    private final String version;

    public RequestLine(String method, String path, String queryString, String version) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.version = version;
    }

    public boolean hasQueryString() {
        return queryString != null;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getVersion() {
        return version;
    }
}
