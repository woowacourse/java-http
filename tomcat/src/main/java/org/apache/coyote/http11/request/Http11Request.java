package org.apache.coyote.http11.request;

import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;

public class Http11Request {
    private final HttpHeaders httpHeaders;
    private final HttpMethod httpMethod;
    private final HttpCookie cookie;

    public Http11Request(HttpHeaders httpHeaders, HttpMethod httpMethod, HttpCookie cookie) {
        this.httpHeaders = httpHeaders;
        this.httpMethod = httpMethod;
        this.cookie = cookie;
    }

    public HttpSession getSession(){
        SessionManager sessionManager = new SessionManager();
        return sessionManager.findSession(cookie.getSession());
    }
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
