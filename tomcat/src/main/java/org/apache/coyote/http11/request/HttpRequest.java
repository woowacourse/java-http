package org.apache.coyote.http11.request;

import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpCookie cookie;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, HttpCookie cookie) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.cookie = cookie;
    }

    public HttpSession getSession() {
        SessionManager sessionManager = new SessionManager();
        return sessionManager.findSession(cookie.getSession());
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
