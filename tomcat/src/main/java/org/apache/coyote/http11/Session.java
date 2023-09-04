package org.apache.coyote.http11;

import nextstep.jwp.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private static final Map<String, User> users = new ConcurrentHashMap<>();

    private Session() {
    }

    public static boolean exist(final String sessionId) {
        return users.containsKey(sessionId);
    }

    public static void login(final String sessionId, final User user) {
        users.put(sessionId, user);
    }
}
