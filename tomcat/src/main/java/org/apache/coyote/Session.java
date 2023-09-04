package org.apache.coyote;

import java.util.UUID;

public class Session {
    private final String sessionId;
    private final Object sessionUser;

    public Session(Object sessionUser) {
        this.sessionId = UUID.randomUUID().toString();
        this.sessionUser = sessionUser;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Object getUser() {
        return sessionUser;
    }
}
