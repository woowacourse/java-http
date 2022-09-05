package org.apache.coyote.servlet.request;

import org.apache.coyote.servlet.cookie.HttpCookie;
import org.apache.coyote.servlet.session.Session;
import org.apache.coyote.support.HttpMethod;

public class HttpRequest {

    private final StartLine startLine;
    private final RequestHeaders headers;
    private final String body;
    private Session session = null;

    public HttpRequest(StartLine startLine, RequestHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public String getUri() {
        return startLine.getUri();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public boolean isMethodOf(HttpMethod method) {
        return startLine.getMethod().equals(method);
    }

    public Parameters getParameters() {
        if (headers.hasParametersAsBody()) {
            return Parameters.of(body);
        }
        return startLine.getParameters();
    }

    public HttpCookie findCookie(String name) {
        final var cookies = headers.getCookies();
        return cookies.getCookie(name);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
