package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class SessionStorage {

    private static final List<Session> sessions = new ArrayList<>();

    public void add(final Session session) {
        sessions.add(session);
    }

    public boolean exist(final String sessionID) {
        return sessions.stream()
                .anyMatch(session -> session.match(sessionID));
    }
}
