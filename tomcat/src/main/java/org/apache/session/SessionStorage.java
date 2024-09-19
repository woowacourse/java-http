package org.apache.session;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionStorage {

    private static final List<Session> sessions = new CopyOnWriteArrayList<>();

    public void add(final Session session) {
        sessions.add(session);
    }

    public boolean exist(final String sessionID) {
        return sessions.stream()
                .anyMatch(session -> session.match(sessionID));
    }
}
