package com.techcourse.db;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.techcourse.model.User;

public class InMemoryUserRepository {

    private static final AtomicLong idCounter = new AtomicLong(1L);
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(idCounter.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    private InMemoryUserRepository() {
    }

    public static User save(User user) {
        User savedUser = new User(idCounter.getAndIncrement(), user.getAccount(), user.getPassword(), user.getEmail());
        database.put(user.getAccount(), savedUser);
        return savedUser;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    public static void deleteByAccount(String account) {
        database.remove(account);
    }
}
