package org.apache.coyote.http11;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.request.HttpRequestData;
import org.apache.coyote.http11.response.HttpResponse;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
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

    public Session findOrCreate(HttpRequestData requestData, HttpResponse response) {

        HttpCookie cookie = new HttpCookie(requestData.headers().get("cookie"));
        String sessionId = cookie.get("JSESSIONID");
        Session session = findSession(sessionId);

        if (session != null) {
            return session;
        }

        session = new Session(UUID.randomUUID().toString());
        add(session);

        response.addHeader(
                "Set-Cookie",
                "JSESSIONID=" + session.getId()
        );

        return session;
    }
}
