package org.apache.coyote.http11;

import com.techcourse.model.User;

public class Session {

    private final String sessionId;
    private final User user;

    public Session(final String sessionId, final User user) {
        this.sessionId = sessionId;
        this.user = user;
    }

    public boolean match(final String sessionId) {
        return this.sessionId.equals(sessionId);
    }
}
