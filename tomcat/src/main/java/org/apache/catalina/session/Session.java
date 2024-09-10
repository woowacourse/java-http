package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    public static final String KEY = "JSESSIONID";

    private final String id;
    private final Map<String, Object> attributes;

    public Session() {
        this.id = randomId();
        this.attributes = new HashMap<>();
    }

    private static String randomId() {
        return UUID.randomUUID().toString();
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public String getId() {
        return id;
    }
}
