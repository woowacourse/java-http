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

    public static User findByAccount(String account, String password) {
        if (!database.containsKey(account)) {
            throw new IllegalArgumentException("[ERROR] No Such User" + account);
        }

        User user = database.get(account);
        user.logCheckPassword(password);
        return user;
    }

    private InMemoryUserRepository() {}
}
