package nextstep.jwp.db;


import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(
                user.getAccount(),
                new User(autoIncrement(), user.getAccount(), user.getPassword(), user.getEmail())
        );
    }

    private synchronized static long autoIncrement() {
        long maxId = database.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L);
        return maxId + 1;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
