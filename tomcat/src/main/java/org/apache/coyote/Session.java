package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;
import com.techcourse.model.User;

public class Session {

    private static final Map<String, User> session = new HashMap<>();

    public static void save(String uuid, User user) {
        session.put(uuid, user);
    }

    public static boolean containsSession(String uuid) {
        return session.containsKey(uuid);
    }
}
