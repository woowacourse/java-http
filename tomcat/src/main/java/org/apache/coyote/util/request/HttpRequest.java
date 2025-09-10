package org.apache.coyote.util.request;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.util.Cookie;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> queries;
    private final Cookie cookie;
    private final Map<String, String> body;

    public HttpRequest(
            String method,
            String path,
            String version,
            Map<String, String> headers,
            Map<String, String> queries,
            Map<String, String> body,
            Cookie cookie
    ) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.queries = queries;
        this.body = body;
        this.cookie = cookie;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Optional<String> getQueryValue(String key) {
        return Optional.ofNullable(queries.get(key));
    }

    public Map<String, String> getBody() {
        return Collections.unmodifiableMap(body);
    }

    public Session getSession(boolean create) {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = SessionManager.getSessionId(cookie);
        if (sessionId != null) {
            Session session = sessionManager.findCustomSession(sessionId);
            if (session != null) {
                return session;
            }
        }
        if (create) {
            String newSessionId = SessionManager.generateSessionId();
            Session newSession = new Session(newSessionId);
            sessionManager.add(newSession);
            return newSession;
        }
        return null;
    }

    public Session changeSessionId() {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = SessionManager.getSessionId(cookie);
        if (sessionId != null) {
            sessionManager.remove(sessionId);
        }
        String newSessionId = SessionManager.generateSessionId();
        Session newSession = new Session(newSessionId);
        sessionManager.add(newSession);
        return newSession;
    }
}
