package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final AtomicLong id = new AtomicLong(1L);
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(id.getAndAdd(1), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static void save(User user) {
        User targetUser = user;
        if (user.getId() == null) {
            targetUser = new User(id.getAndAdd(1), user.getAccount(), user.getPassword(), user.getEmail());
        }
        database.put(user.getAccount(), targetUser);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
