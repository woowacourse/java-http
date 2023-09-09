package org.apache.coyote.http11.session;

import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {

    private static Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static void add(Session session) {
        sessions.put(session.getId(), session);
    }

    public static Optional<Session> findSession(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public static void remove(Session session) {
        sessions.remove(session.getId());
    }

    public static boolean hasSessionWithAttributeType(String sessionId, Class<?> attributeType) {
        final Optional<Session> session = findSession(sessionId);
        return session.map(value -> value.hasAttributeType(attributeType))
                .orElse(false);
    }
}
