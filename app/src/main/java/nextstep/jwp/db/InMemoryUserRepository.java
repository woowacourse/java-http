package nextstep.jwp.db;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long nextId = 2L;

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        if (existsByAccount(user.getAccount())) {
            throw new IllegalArgumentException("This user already exists");
        }

        database.put(user.getAccount(), new User(nextId++, user));
    }

    public static boolean existsByAccountAndPassword(final String account, final String password) {
        final Optional<User> user = findByAccount(account);

        return user.isPresent() && user.get().checkPassword(password);
    }

    public static boolean existsByAccount(final String account) {
        return database.containsKey(account);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
