package com.techcourse.db;

import com.techcourse.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccountAndPassword(String account, String password) {
        if (database.containsKey(account) && database.get(account).checkPassword(password)) {
            return Optional.ofNullable(database.get(account));
        }
        return Optional.empty();
    }

    public static boolean existsByAccount(String account) {
        return database.containsKey(account);
    }

    private InMemoryUserRepository() {}
}
