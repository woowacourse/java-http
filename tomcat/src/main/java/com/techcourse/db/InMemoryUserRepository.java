package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong id;

    static {
        id = new AtomicLong(1L);
        final User user = new User(id.getAndIncrement(), "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static User save(User user) {
        user.setId(id.getAndIncrement());
        database.put(user.getAccount(), user);

        return user;
    }

    public static Optional<User> findByAccountAndPassword(String account, String password) {
        return findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .stream()
                .findFirst();
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
