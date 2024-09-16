package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.cookie.Cookie;

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

    public String getRequestBody() {
        return requestBody.getRequestBody();
    }

    public QueryParameters getQueryParameters() {
        return requestBody.getQueryParameters();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Cookie getCookie() {
        if (requestHeader.getCookies() == null) {
            return new Cookie();
        }

        return new Cookie(requestHeader.getCookies());
    }
}
