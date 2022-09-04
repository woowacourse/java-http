package org.apache.coyote.servlet.request;

import java.util.Optional;
import org.apache.coyote.servlet.cookie.HttpCookie;
import org.apache.coyote.servlet.session.Session2;
import org.apache.coyote.support.HttpMethod;

public class HttpRequest {

    private final StartLine startLine;
    private final RequestHeaders headers;
    private final String body;
    private final Session2 session;

    public HttpRequest(StartLine startLine, RequestHeaders headers, String body, Session2 session) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
        this.session = session;
    }

    public boolean isMethodOf(HttpMethod method) {
        return startLine.hasMethodOf(method);
    }

    public String getUri() {
        return startLine.getUri();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public Parameters getParameters() {
        if (headers.hasParametersAsBody()) {
            return Parameters.of(body);
        }
        return startLine.getParameters();
    }

    public Optional<HttpCookie> findCookie(String name) {
        final var cookies = headers.getCookies();
        final var cookie = cookies.getCookie(name);
        if (cookie == null) {
            return Optional.empty();
        }
        return Optional.of(cookie);
    }

    public Session2 getSession() {
        return session;
    }
}
