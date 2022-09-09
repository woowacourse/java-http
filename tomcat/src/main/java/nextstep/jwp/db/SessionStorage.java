package nextstep.jwp.db;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {

    private static final Map<String, String> sessions = new ConcurrentHashMap<>();

    private static String save(String account) {
        String value = UUID.randomUUID().toString();
        sessions.put(account, value);
        return value;
    }

    public static String getSession(String account) {
        return sessions.getOrDefault(account, save(account));
    }

    private SessionStorage() {

    }
}
