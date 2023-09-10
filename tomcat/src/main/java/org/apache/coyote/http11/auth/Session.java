package org.apache.coyote.http11.auth;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.User;

public class Session {

    private final String id;
    private final Map<String, User> items = new HashMap<>();

    private Session(final String id) {
        this.id = id;
    }

    public static Session from(String id) {
        return new Session(id);
    }

    public void setAttribute(final String key, final User value) {
        items.put(key, value);
    }

    public String getId() {
        return id;
    }

}
