package org.apache.coyote.http11.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.header.Cookies;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionManager {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
        throw new UnsupportedOperationException("SessionManager 객체를 생성할 수 없습니다.");
    }

    public static Session getSession(final HttpRequest request) {
        Cookies cookies = request.getCookies();

        if (cookies.hasSessionId() && SESSIONS.containsKey(cookies.getSessionId())) {
            return SESSIONS.get(cookies.getSessionId());
        }

        return createSession();
    }

    private static Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }
}
