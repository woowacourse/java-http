package kokodak.http;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void addSession(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.getOrDefault(id, new Session(null));
    }

    public static void removeSession(final String id) {
        SESSIONS.remove(id);
    }
}
