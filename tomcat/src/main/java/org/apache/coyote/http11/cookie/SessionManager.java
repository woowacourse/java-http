package org.apache.coyote.http11.cookie;

import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<User, String> sessions = new HashMap<>();

    public static void add(final User user, HttpCookie httpCookie) {
        sessions.put(user, httpCookie.getJSessionId());
    }

    public static boolean isValidUser(final User user, final String jsessionId) {
        return sessions.entrySet().stream()
                .anyMatch(userStringEntry ->
                        userStringEntry.getKey().equals(user)
                        && userStringEntry.getValue().equals(jsessionId)
                );
    }

    public static boolean isValidHttpCookie(final HttpCookie httpCookie) {
        return sessions.containsValue(httpCookie.getJSessionId());
    }
}
