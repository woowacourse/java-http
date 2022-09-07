package nextstep.jwp.db;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {

    private static final Map<Long, String> sessions = new ConcurrentHashMap<>();

    private static String save(Long id) {
        String value = UUID.randomUUID().toString();
        sessions.put(id, value);
        return value;
    }

    public static String getSession(Long id) {
        return sessions.getOrDefault(id, save(id));
    }

    private SessionStorage() {

    }
}
