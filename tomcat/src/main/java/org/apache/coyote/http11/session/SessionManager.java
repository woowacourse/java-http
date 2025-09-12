package org.apache.coyote.http11.session;

import com.techcourse.model.Account;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Session create() {
        Session newSession = new Session(UUID.randomUUID().toString());
        add(newSession);

        return newSession;
    }

    public static void invalidateAllForUser(final Account account) {
        SESSIONS.values().removeIf(session -> {
            User user = (User) session.getAttribute("user");
            if (user.checkAccount(account)) {
                session.invalidate();
                return true;
            }
            return false;
        });
    }

    public static Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    private static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }
}
