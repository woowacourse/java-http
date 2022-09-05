package org.apache.coyote.manager;

import nextstep.jwp.model.User;

public class Session {

    private final String id;
    private final User user;

    public Session(final String id, final User user) {
        this.id = id;
        this.user = user;
    }
}
