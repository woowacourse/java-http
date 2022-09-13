package session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class SessionManager {

    private static final Map<User, Session> sessions = new ConcurrentHashMap<>();

    public static void addSession(final User user, final Session session) {
        sessions.put(user, session);
    }
}
