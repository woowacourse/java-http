package org.apache.catalina;

import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    public static User get(String sessionId) {
        User user = sessions.get(sessionId);
        if (user == null) {
            throw new IllegalArgumentException("No such user exists.");
        }
        return user;
    }

    public static String put(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }
}
