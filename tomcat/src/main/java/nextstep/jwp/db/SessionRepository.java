package nextstep.jwp.db;

import nextstep.jwp.model.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionRepository {

    private static final Map<Long, Session> sessions = new HashMap<>();

    public static UUID save(Long userId) {
        UUID uuid = UUID.randomUUID();
        sessions.put(userId, new Session(uuid));
        System.out.println(sessions.size());
        return uuid;
    }

    public static boolean islogin(final String id) {
        return sessions.values().stream().anyMatch(session -> session.getId().equals(id));
    }
    public Session findSession(final String id) {
        return sessions.get(id);
    }

    public void remove(final String id) {
        sessions.remove(id);
    }

    private SessionRepository() {}
}