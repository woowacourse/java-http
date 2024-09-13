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
        User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);

        SessionManager sessionManager = new SessionManager();
        Session newSession = new Session("gugu");
        newSession.setAttribute("JSESSIONID",UUID.randomUUID().toString());
        sessionManager.add(newSession);
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
