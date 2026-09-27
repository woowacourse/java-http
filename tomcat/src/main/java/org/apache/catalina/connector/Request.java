package org.apache.catalina.connector;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.RequestLine;

public final class Request {
    private final HttpRequest httpRequest;
    private final Manager sessionManager;
    private Session session;

    public Request(final HttpRequest httpRequest, final Manager sessionManager) {
        this.httpRequest = httpRequest;
        this.sessionManager = sessionManager;
    }

    public RequestLine requestLine() {
        return httpRequest.requestLine();
    }

    public String path() {
        return httpRequest.path();
    }

    public Map<String, String> formParameters() {
        return httpRequest.formParameters();
    }

    public Optional<String> requestedSessionId() {
        return httpRequest.cookie(HttpCookie.JSESSIONID);
    }

    public Session getSession(final boolean create) throws IOException {
        if (session == null) {
            session = findRequestedSession();
        }
        if (session == null && create) {
            session = sessionManager.createSession();
        }
        return session;
    }

    private Session findRequestedSession() throws IOException {
        final Optional<String> sessionId = requestedSessionId();
        if (sessionId.isEmpty()) {
            return null;
        }
        return sessionManager.findSession(sessionId.get());
    }
}
