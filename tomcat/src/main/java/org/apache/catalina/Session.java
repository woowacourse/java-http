package org.apache.catalina;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;

public class Session {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

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

    public synchronized Object getValue(String key) {
        return values.get(key);
    }

    public void putValue(String key, Object value) {
        values.put(key, value);
    }

}
