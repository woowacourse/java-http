package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository {

    private static final InMemoryUserRepository INSTANCE = new InMemoryUserRepository();
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    private final AtomicLong id = new AtomicLong();

    static {
        INSTANCE.save(new User("gugu", "password", "hkkang@woowahan.com"));
    }

    public static InMemoryUserRepository getInstance() {
        return INSTANCE;
    }

    public void save(User user) {
        user.setId(id.getAndIncrement());
        database.put(user.getAccount(), user);
    }

    public Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
