package com.techcourse.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.techcourse.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new HashMap<>();

    static {
        final var user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static void save(final User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(final String account) {
        return Optional.ofNullable(database.get(account));
    }
}
