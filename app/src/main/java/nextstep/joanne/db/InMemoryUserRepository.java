package nextstep.joanne.db;


import nextstep.joanne.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {
    private static long userId;
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
