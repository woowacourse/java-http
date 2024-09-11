package com.techcourse.db;

import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class InMemoryUserRepository {

    private static final Map<String, User> database = new ConcurrentHashMap<>();

    static {
        final User user = new User(1L, "gugu", "password", "hkkang@woowahan.com");
        database.put(user.getAccount(), user);
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        session.setAttribute("gugu", user);
        SessionManager.getInstance().add(session);
    }

    public static void save(User user) {
        database.put(user.getAccount(), user);
    }

    public static Optional<User> findByAccount(String account) {
        return Optional.ofNullable(database.get(account));
    }

    private InMemoryUserRepository() {
    }
}
