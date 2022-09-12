package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.model.User;

public class Session {

    private static final String USER = "user";
    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public Optional<Object> getUserAttribute() {
        return Optional.ofNullable(values.get(USER));
    }

    public void setUserAttribute(User user) {
        values.put(USER, user);
    }

    public String getId() {
        return id;
    }
}
