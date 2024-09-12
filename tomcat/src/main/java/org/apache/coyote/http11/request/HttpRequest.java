package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {
    private final HttpRequestLine requestLine;
    private final HttpRequestHeader requestHeader;
    private final HttpRequestBody requestBody;

    public HttpRequest(HttpRequestLine requestLine, HttpRequestHeader requestHeader, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader.getHeaders();
    }

    public Map<String, String> getRequestBody() {
        return requestBody.getBodies();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }
}
