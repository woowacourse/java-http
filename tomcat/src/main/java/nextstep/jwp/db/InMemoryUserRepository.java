package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final long INITIAL_KEY_VALUE = 1L;
    private static final AtomicLong id = new AtomicLong(INITIAL_KEY_VALUE);

    static {
        final User user = new User(id.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        final User savedUser = new User(id.getAndIncrement(), user.getAccount(), user.getPassword(), user.getEmail());
        database.put(savedUser.getAccount(), savedUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static void clear() {
        database.clear();
    }

    private InMemoryUserRepository() {
    }
}
