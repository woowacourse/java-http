package nextstep.jwp.dashboard.db;


import nextstep.jwp.dashboard.domain.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long autoIncrement = 1L;

    static {
        final User user = new User(autoIncrement++, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        user.setId(autoIncrement++);
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static boolean existsByAccount(String account) {
        return database.containsKey(account);
    }
}
