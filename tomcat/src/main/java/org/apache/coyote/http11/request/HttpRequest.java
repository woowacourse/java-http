package org.apache.coyote.http11.request;

import org.apache.catalina.HttpSession;
import org.apache.catalina.SessionManager;

public class HttpRequest {
    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpCookie cookie;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, HttpCookie cookie,
                       String requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.cookie = cookie;
        this.requestBody = requestBody;
    }

    public HttpSession getSession() {
        SessionManager sessionManager = new SessionManager();
        String session = cookie.getSession();
        if (session == null) {
            return new HttpSession();
        }
        return sessionManager.findSession(session);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getRequestBody() {
        return requestBody;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", httpHeaders=" + httpHeaders +
                ", cookie=" + cookie +
                ", requestBody='" + requestBody + '\'' +
                '}';
    }
}
