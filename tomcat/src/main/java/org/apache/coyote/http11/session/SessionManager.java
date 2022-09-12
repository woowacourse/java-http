package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.User;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static Session create(final User user) {
        final Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        session.setAttribute("user", user);
        return session;
    }
}
