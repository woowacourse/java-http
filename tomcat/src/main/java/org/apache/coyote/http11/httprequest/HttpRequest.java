package org.apache.coyote.http11.httprequest;

import java.util.Map;
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

    public boolean isMethod(HttpMethod method) {
        return httpRequestLine.isMethod(method);
    }

    public boolean isPath(String path) {
        return httpRequestLine.isPath(path);
    }

    public boolean containsHeader(String key) {
        return httpRequestHeader.containsHeader(key);
    }

    public boolean containsHeader(HttpHeaderName httpHeaderName) {
        return httpRequestHeader.containsHeader(httpHeaderName);
    }

    public String getHeaderValue(String key) {
        return httpRequestHeader.getHeaderValue(key);
    }

    public String getHeaderValue(HttpHeaderName httpHeaderName) {
        return httpRequestHeader.getHeaderValue(httpHeaderName);
    }

    public boolean containsBody(String key) {
        return httpRequestBody.containsBody(key);
    }

    public String getBodyValue(String key) {
        return httpRequestBody.getBodyValue(key);
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

    public Map<String, String> getBody() {
        return httpRequestBody.getBody();
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public HttpRequestBody getHttpRequestBody() {
        return httpRequestBody;
    }
}
