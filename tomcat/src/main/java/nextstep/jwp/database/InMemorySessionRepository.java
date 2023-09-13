package nextstep.jwp.database;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository {

    private static final Map<Long, UUID> sessions = new ConcurrentHashMap<>();

    private InMemorySessionRepository() {
    }

    public static UUID save(Long userId) {
        UUID uuid = UUID.randomUUID();
        sessions.put(userId, uuid);
        return uuid;
    }
}
