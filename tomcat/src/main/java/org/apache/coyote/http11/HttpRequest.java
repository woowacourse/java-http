package org.apache.coyote.http11;

import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String requestBody;
    private Session session;
    private boolean sessionCreated;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpMethod method() {
        return requestLine.method();
    }

    public String path() {
        return requestLine.path();
    }

    public RequestLine line() {
        return requestLine;
    }

    public HttpCookie cookie() {
        return HttpCookie.from(headers.valueOf("Cookie"));
    }

    public String requestBody() {
        return requestBody;
    }

    public Session getSession() {
        return getSession(true);
    }

    public Session getSession(final boolean create) {
        if (session != null) {
            return session;
        }
        final SessionManager sessionManager = SessionManager.getInstance();
        final String jSessionId = cookie().getValue("JSESSIONID");

        session = sessionManager.findSession(jSessionId);
        if (session == null && create) {
            session = SessionProvider.provide();
            sessionCreated = true;
        }
        return session;
    }

    public Optional<Session> createdSession() {
        if (sessionCreated) {
            return Optional.of(session);
        }
        return Optional.empty();
    }
}
