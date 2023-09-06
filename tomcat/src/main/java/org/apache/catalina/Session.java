package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.User;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public String addUser(User user) {
        final var key = UUID.randomUUID().toString();
        values.put(key, user);
        return key;
    }

    public Map<String, Object> getValues() {
        return values;
    }

}
