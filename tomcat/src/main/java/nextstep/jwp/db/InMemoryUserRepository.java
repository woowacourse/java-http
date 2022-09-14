package nextstep.jwp.db;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(final User user) {
        database.put(user.getAccount(), User.from(getNewId(), user));
    }

    public static Optional<User> findByAccount(final String account) {
        return Optional.ofNullable(database.get(account));
    }

    private static Long getNewId() {
        return database.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L) + 1;
    }

    public static void deleteAll() {
        database.clear();
    }
}
