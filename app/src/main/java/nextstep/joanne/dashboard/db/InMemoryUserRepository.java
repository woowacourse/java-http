package nextstep.joanne.dashboard.db;


import nextstep.joanne.dashboard.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {
    private static long userId = 0;
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    private InMemoryUserRepository() {
    }

    static {
        final User user = new User(++userId, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        user.setId(++userId);
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
