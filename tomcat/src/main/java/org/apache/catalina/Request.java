package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;

public class Request {

    private static final String JSESSIONID = "JSESSIONID";

    private final HttpRequest httpRequest;
    private final SessionManager sessionManager;

    private Session createdSession;

    public Request(final HttpRequest httpRequest, final SessionManager sessionManager) {
        this.httpRequest = httpRequest;
        this.sessionManager = sessionManager;
    }

    public Optional<Session> findSession() {
        return sessionManager.find(httpRequest.cookies().get(JSESSIONID));
    }

    public Session createSession() {
        createdSession = sessionManager.create();

        return createdSession;
    }

    public Optional<Session> createdSession() {
        return Optional.ofNullable(createdSession);
    }

    public Map<String, String> body() {
        return httpRequest.body();
    }

    public boolean isGet() {
        return httpRequest.isGet();
    }

    public boolean isPost() {
        return httpRequest.isPost();
    }
}
