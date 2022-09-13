package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;
    private Session session;

    public HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                       final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest ofRequestLine(final String line) {
        return new HttpRequest(RequestLine.of(line), null, null);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getContentType() {
        return requestLine.getContentType();
    }

    public String getResource() {
        return requestLine.getResource();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public String getCookie() {
        return requestHeaders.getCookie();
    }

    public String extractSessionId() {
        return requestHeaders.getCookieSessionID();
    }

    public void addSession(final Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
