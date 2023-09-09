package nextstep.jwp.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final AtomicLong id = new AtomicLong(1L);

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(id.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        final User userWithId = new User(id.getAndIncrement(), user.getAccount(), user.getPassword(), user.getEmail());
        database.put(user.getAccount(), userWithId);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static boolean checkExistingId(final String account) {
        Optional<User> user = findByAccount(account);
        return user.isPresent();
    }

    private InMemoryUserRepository() {
    }
}
