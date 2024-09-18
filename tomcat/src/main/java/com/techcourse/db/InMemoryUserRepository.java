package com.techcourse.db;

import com.techcourse.model.User;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static AtomicLong ID_GENERATOR = new AtomicLong(1);

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User("gugu", "password", "hkkang@woowahan.com");
        save(user);
    }

    public static void save(User user) {
        setId(user);
        database.put(user.getAccount(), user);
    }

    private static void setId(User user) {
        try {
            Field field = user.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, ID_GENERATOR.getAndIncrement());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
