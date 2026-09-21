package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User gugu = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        final User milan = new User(1L, "1", "1", "1@woowahan.com");
        database.put(gugu.getAccount(), gugu);
        database.put(milan.getAccount(), milan);

    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
