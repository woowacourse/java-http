package com.techcourse.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.techcourse.model.User;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static User findByAccount(String account) {
        return database.get(account);
    }

    private InMemoryUserRepository() {
    }
}
