package com.techcourse.db;

import com.techcourse.model.User;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository {
    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        User gugu = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(gugu.getAccount(), gugu);

        Session newSession = new Session(gugu.getAccount());
        SessionManager.add(newSession);
    }

    private InMemoryUserRepository() {

    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }
}
