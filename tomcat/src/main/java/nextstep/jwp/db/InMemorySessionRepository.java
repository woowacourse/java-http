package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;

public class InMemorySessionRepository {

    private static final Map<User, Session> database = new ConcurrentHashMap<>();

    static {
        database.put(
            InMemoryUserRepository.findByAccount("gugu").get(),
            new Session(UUID.randomUUID().toString())
        );
    }

    private InMemorySessionRepository() {
    }

    public static Session save(final User user, final Session session) {
        database.put(user, session);
        
        return session;
    }

    public static Optional<Session> findByUser(final User user) {
        if (database.containsKey(user)) {
            return Optional.of(database.get(user));
        }
        return Optional.empty();
    }

    public static boolean isExist(final Session session) {
        return database.containsValue(session);
    }
}
