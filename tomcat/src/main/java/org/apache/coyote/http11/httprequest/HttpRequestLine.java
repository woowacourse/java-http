package org.apache.coyote.http11.httprequest;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpRequestLine(String requestLine) {
        String[] headerFirstLine = requestLine.split(" ");
        this.method = HttpMethod.getHttpMethod(headerFirstLine[0]);
        this.path = headerFirstLine[1];
        this.version = headerFirstLine[2];
    }

    public boolean isMethod(String name) {
        return method.isMethod(name);
    }

    public boolean isPath(String path) {
        return this.path.equals(path);
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
