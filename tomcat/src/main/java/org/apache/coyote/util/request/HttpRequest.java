package org.apache.coyote.util.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.util.Cookie;
import org.apache.coyote.util.Session;
import org.apache.coyote.util.SessionManager;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> queries;
    private final Cookie cookie;

    public HttpRequest(String method, String path, String version, Map<String, String> queries, Cookie cookie) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.queries = queries != null ? queries : new HashMap<>();
        this.cookie = cookie != null ? cookie : Cookie.parse(null);
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

    public Cookie getCookie() {
        return cookie;
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
}
