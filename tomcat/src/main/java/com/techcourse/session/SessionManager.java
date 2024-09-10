package com.techcourse.session;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> sessions = new HashMap<>();
    private static final String ATTRIBUTE_USER = "user";

    private SessionManager() {
    }

    public static String addUser(User user) {
        Session session = new Session();
        session.setAttribute(ATTRIBUTE_USER, user);
        sessions.put(session.getId(), session);
        return session.getId();
    }
}
