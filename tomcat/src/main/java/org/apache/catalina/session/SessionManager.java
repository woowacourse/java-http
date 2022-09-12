package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager () {
    }

    public static Session generate(final Map<String, Object> attributes) {
        final Session session = Session.generate();
        session.setAttributes(attributes);

        sessions.put(session.getId(), session);
        return session;
    }

    public static boolean exist(final String id) {
        return sessions.containsKey(id);
    }
}
