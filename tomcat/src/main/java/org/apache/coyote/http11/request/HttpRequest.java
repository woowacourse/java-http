package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest ofRequestLine(String line) {
        return new HttpRequest(RequestLine.of(line), null, null);
    }

    public Map<String, String> getQueries() {
        return requestLine.getQueries();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getContentType() {
        return requestLine.getContentType();
    }

    public String getResource() {
        return requestLine.getResource();
    }

    public boolean hasQueryString() {
        return getPath().contains("?");
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public boolean hasSessionCookie() {
        return requestHeaders.hasSessionCookie();
    }

    public String getCookie() {
        return requestHeaders.getCookie();
    }
}
