package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.model.User;

public class SessionFactory {

    private final Map<User, String> sessions;

    private SessionFactory(final Map<User, String> sessions) {
        this.sessions = sessions;
    }

    public static SessionFactory init() {
        return new SessionFactory(new HashMap<>());
    }

    public void add(final User user, final HttpResponse httpResponse) {
        sessions.put(user, httpResponse.getSessionId());
    }
}
