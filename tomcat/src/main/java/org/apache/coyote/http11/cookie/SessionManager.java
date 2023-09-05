package org.apache.coyote.http11.cookie;

import nextstep.jwp.model.User;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<User, String> sessions = new HashMap<>();

    public static void add(final User user, String jsessionId) {
        sessions.put(user, jsessionId);
    }

    public static boolean validUser(final User user) {
        if(sessions.containsKey(user)) {
            return true;
        }

        return false;
    }

    public static boolean validJsession(final String jsession) {
        if(sessions.containsValue(jsession)) {
            return true;
        }

        return false;
    }
}
