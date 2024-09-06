package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "sancho", "1234", "sancho@woowa.com");
        database.put(user.getAccount(), user);
    }

    public static User save(User user) {
        return database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static Optional<User> findByAccountAndPassword(String account, String password) {
        User user = database.get(account);
        if (user != null && user.checkPassword(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    private InMemoryUserRepository() {
    }
}
