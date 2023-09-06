package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(1);
    private static final long DEFAULT_INCREASE_SIZE = 1L;

    static {
        final User user = new User(sequence.getAndAdd(DEFAULT_INCREASE_SIZE), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(final User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(final String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static boolean existsByAccount(final String account) {
        return database.containsKey(account);
    }

    private InMemoryUserRepository() {}
}
