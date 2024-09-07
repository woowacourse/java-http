package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static Long nextId = 1L;

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        save(user);
    }

    public static void save(User user) {
        User newUser = new User(nextId++, user.getAccount(), user.getPassword(), user.getEmail());
        database.put(newUser.getAccount(), newUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
