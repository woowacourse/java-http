package nextstep.jwp.db;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionRepository {

    private static final Map<Long, UUID> sessions = new ConcurrentHashMap<>();

    public static UUID save(Long userId) {
        UUID uuid = UUID.randomUUID();
        sessions.put(userId, uuid);
        return uuid;
    }
}
