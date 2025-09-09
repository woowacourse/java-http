package org.apache.coyote.http11;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class SessionManager  {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        return SESSIONS.getOrDefault(id, null);
    }

    public static void remove(final String id) {
        SESSIONS.remove(id);
    }

    public static User getUser(final Session session) {
        return (User) session.getAttribute("user");
    }

    private SessionManager() {}
}
