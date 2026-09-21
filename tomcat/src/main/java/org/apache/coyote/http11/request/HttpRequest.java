package org.apache.coyote.http11.request;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Optional;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    private Session newSession;

    public HttpRequest(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body
    ) {
        return new HttpRequest(requestLine, headers, body);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();

    }
    public Optional<String> getParameter(String name) {
        return body.getParameter(name);
    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }

    public Optional<Session> findSession() {
        if (newSession != null) {
            return Optional.of(newSession);
        }
        return getCookie().get(HttpCookie.JSESSIONID)
                .flatMap(SessionManager.getInstance()::findSession);
    }

    public Session getSession() {
        return findSession().orElseGet(this::createSession);
    }

    public Optional<Session> getNewSession() {
        return Optional.ofNullable(newSession);
    }

    private Session createSession() {
        newSession = SessionManager.getInstance().create();
        return newSession;
    }
}
