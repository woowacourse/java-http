package nextstep.jwp.db;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.Session;

public class InMemorySessionRepository {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private InMemorySessionRepository() {
    }

    public static void save(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session getSession(final String id) {
        if (!hasSession(id)) {
            throw new IllegalArgumentException(String.format("Cannot find session.(%s)", id));
        }
        return SESSIONS.get(id);
    }

    public static boolean hasSession(final String id) {
        return SESSIONS.containsKey(id);
    }
}
