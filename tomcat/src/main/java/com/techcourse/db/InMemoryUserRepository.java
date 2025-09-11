package com.techcourse.db;

import com.techcourse.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {
    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1L);

    static {
        final User user = new User(ID_GENERATOR.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static Long generateId() {
        return ID_GENERATOR.getAndIncrement();
    }

    private InMemoryUserRepository() {}
}
