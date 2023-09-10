package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpRequestLine(String method, String path, String version) {
        this(HttpMethod.from(method), extractFromQueryString(path), version);
    }

    public HttpRequestLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    private static String extractFromQueryString(String path) {
        if (!path.contains("?")) {
            return path;
        }
        return path.substring(0, path.indexOf("?"));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
