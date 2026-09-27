package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void setSessionCookie(HttpRequest request, HttpResponse response) {
        HttpCookie cookie = new HttpCookie(request.getHeader("Cookie"));
        if (cookie.get("JSESSIONID") == null) {
            response.setHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        }
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public void remove(final Session session) {
        remove(session.getId());
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}
