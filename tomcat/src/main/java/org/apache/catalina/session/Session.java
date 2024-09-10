package org.apache.catalina.session;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class Session {

    private static final String USER_SESSION_NAME = "user";

    private final Map<String, Object> values = new HashMap<>();

    private Session() {
    }

    public static Session ofUser(User user) {
        Session session = new Session();
        session.values.put(USER_SESSION_NAME, user);
        return session;
    }
}
