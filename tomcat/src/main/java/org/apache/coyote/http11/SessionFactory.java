package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.model.User;

public class SessionFactory {

    private final Map<User, String> sessions;

    private SessionFactory(final Map<User, String> sessions) {
        this.sessions = sessions;
    }

    public static SessionFactory init() {
        return new SessionFactory(new HashMap<>());
    }
}
