package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;

public class Session {

    private static final String USER = "user";

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addUser(final User user) {
        values.put(USER, user);
    }

    public User getUser() {
        final Object value = values.get(USER);
        if (Objects.isNull(value)) {
            throw new UserNotFoundException(id);
        }

        return (User) value;
    }
}
