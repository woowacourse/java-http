package org.apache.coyote.util.request;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.Session;
import org.apache.coyote.SessionManager;
import org.apache.coyote.util.Cookie;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> body;
    private final Cookie cookie;
    private SessionManager sessionManager;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, Map<String, String> body, Cookie cookie) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.cookie = cookie;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(body);
    }

    public Session getSession(boolean create) {
        if (sessionManager == null) {
            throw new IllegalStateException("No SessionManager is configured.");
        }
        String sessionId = sessionManager.getSessionId(cookie);
        if (sessionId != null) {
            Session session = sessionManager.findSession(sessionId);
            if (session != null) {
                return session;
            }
        }
        if (create) {
            return sessionManager.createSession();
        }
        return null;
    }

    public Session changeSessionId() {
        if (sessionManager == null) {
            throw new IllegalStateException("No SessionManager is configured.");
        }
        String sessionId = sessionManager.getSessionId(cookie);
        if (sessionId != null) {
            sessionManager.remove(sessionId);
        }
        return sessionManager.createSession();
    }
}
