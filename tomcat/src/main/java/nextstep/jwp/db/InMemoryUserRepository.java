package nextstep.jwp.db;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static AtomicLong counter = new AtomicLong();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static long save(User user) {
        final long andIncrement = counter.getAndIncrement();
        database.put(user.getAccount(), user.setId(andIncrement));
        return andIncrement;
    }

    public static Optional<User> findByAccount(String account) {
        if (database.keySet().stream().anyMatch(key -> key.equals(account))) {
            return Optional.of(database.get(account));
        }
        return Optional.empty();
    }

    private InMemoryUserRepository() {}
}
