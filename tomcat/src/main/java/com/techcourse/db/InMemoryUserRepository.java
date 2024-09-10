package com.techcourse.db;

import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserInfo;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();
    private static final AtomicLong id = new AtomicLong(2);

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
    }

    public static User save(UserInfo userInfo) {
        User user = new User(id.getAndIncrement(), userInfo.account(), userInfo.password(), userInfo.email());
        database.put(user.getAccount(), user);

        return user;
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
