package org.apache.coyote.http11.protocol.request;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.protocol.cookie.Cookie;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody)
            throws IOException {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String key) {
        return requestHeaders.getHeader(key);
    }

    public Map<String, String> getHeaders() {
        return requestHeaders.getHeaders();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getParameter(String key) {
        if (getMethod() == HttpMethod.GET) {
            return requestLine.getQueryParameter(key);
        }
        return requestBody.get(key);
    }

    public Map<String, String> getParameters() {
        return requestLine.getQueryParameters();
    }

    public Cookie getCookie() {
        if (requestHeaders.getCookieString() == null) {
            return null;
        }
        return new Cookie(requestHeaders.getCookieString());
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}

