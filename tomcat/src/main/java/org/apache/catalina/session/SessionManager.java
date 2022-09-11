package org.apache.catalina.session;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap();

    private SessionManager () {
    }

    public static Session generate(final Map<String, Object> attributes) {
        final Session session = Session.generate();
        session.setAttributes(attributes);

        sessions.put(session.getId(), session);
        return session;
    }

    public static Session findSession(final String id) {
        if (!sessions.containsKey(id)) {
            throw new NoSuchElementException("id에 해당하는 세션을 찾지 못했습니다 : " + id);
        }
        return sessions.get(id);
    }
}
