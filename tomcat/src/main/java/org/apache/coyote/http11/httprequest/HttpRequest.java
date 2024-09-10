package org.apache.coyote.http11.httprequest;

import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(HttpRequestLine httpRequestLine, HttpRequestHeader httpRequestHeader, HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
    }

    public HttpRequest(HttpRequestLine httpRequestLine, HttpRequestHeader httpRequestHeader) {
        this(httpRequestLine, httpRequestHeader, null);
    }

    public boolean isMethod(String name) {
        return httpRequestLine.isMethod(name);
    }

    public boolean isPath(String path) {
        return httpRequestLine.isPath(path);
    }

    public boolean containsKey(String key) {
        return httpRequestHeader.containsKey(key);
    }

    public boolean containsKey(HttpHeaderName httpHeaderName) {
        return httpRequestHeader.containsKey(httpHeaderName);
    }

    public String getValue(String key) {
        return httpRequestHeader.getValue(key);
    }

    public String getValue(HttpHeaderName httpHeaderName) {
        return httpRequestHeader.getValue(httpHeaderName);
    }

    public HttpMethod getMethod() {
        return httpRequestLine.getMethod();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public String getVersion() {
        return httpRequestLine.getVersion();
    }

    public String getBody() {
        return httpRequestBody.getBody();
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }
}
