package com.techcourse.db;

import com.techcourse.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final int INITIAL_ID_VALUE = 1;

    private static final Map<String, User> DATABASE = new ConcurrentHashMap<>();
    private static final AtomicLong USER_ID = new AtomicLong(INITIAL_ID_VALUE);

    static {
        save(new User("gugu", "password", "hkkang@woowahan.com"));
    }

    private InMemoryUserRepository() {
    }

    public static void save(User user) {
        user.setId(USER_ID.getAndIncrement());
        DATABASE.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(DATABASE.get(account));
    }
}
