package org.apache.coyote.http11.httprequest;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private static final String HEADER_DELIMITER = " ";

    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpRequestLine(String requestLine) {
        String[] headerFirstLine = requestLine.split(HEADER_DELIMITER);
        this.method = HttpMethod.getHttpMethod(headerFirstLine[0]);
        this.path = headerFirstLine[1];
        this.version = headerFirstLine[2];
    }

    public boolean isMethod(HttpMethod method) {
        return method.isMethod(method);
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
