package org.apache.coyote.http11.request;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Optional;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;
    private final SessionManager sessionManager;

    private Session newSession;

    public HttpRequest(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body,
            final SessionManager sessionManager
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.sessionManager = sessionManager;
    }

    public static HttpRequest of(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body,
            final SessionManager sessionManager
    ) {
        return new HttpRequest(requestLine, headers, body, sessionManager);
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
                .flatMap(sessionManager::findSession);
    }

    public Session getSession() {
        return findSession().orElseGet(this::createSession);
    }

    public Optional<Session> getNewSession() {
        return Optional.ofNullable(newSession);
    }

    private Session createSession() {
        newSession = sessionManager.create();
        return newSession;
    }
}
