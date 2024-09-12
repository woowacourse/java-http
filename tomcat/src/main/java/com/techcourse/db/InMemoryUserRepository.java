package com.techcourse.db;

import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserRegistration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong id = new AtomicLong(1);

    static {
        UserRegistration userRegistration = new UserRegistration("gugu", "password", "hkkang@woowahan.com");
        save(userRegistration);
    }

    public static User save(UserRegistration userRegistration) {
        User user = new User(id.getAndIncrement(), userRegistration.account(), userRegistration.password(), userRegistration.email());
        database.put(user.getAccount(), user);

        return user;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
