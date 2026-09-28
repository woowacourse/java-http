package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }

        return SESSIONS.getOrDefault(id, null);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public Session findSession(HttpRequest request) {
        HttpCookie cookie =
                new HttpCookie(request.getHeader("cookie"));

        String sessionId = cookie.get("JSESSIONID");

        return findSession(sessionId);
    }

    public Session createSession(HttpResponse response) {
        Session session =
                new Session(UUID.randomUUID().toString());

        add(session);

        response.addHeader(
                "Set-Cookie",
                "JSESSIONID=" + session.getId()
        );

        return session;
    }
}
