package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private static final String JSESSIONID = "JSESSIONID";

    private final String id;
    private final Map<String, Object> attributes;

    private Session(final String id, final Map<String, Object> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public static Session generate() {
        final String id = UUID.randomUUID().toString();
        final Map<String, Object> attributes = new ConcurrentHashMap<>();

        return new Session(id, attributes);
    }

    public String asLine() {
        return JSESSIONID + "=" + id;
    }

    public void setAttribute(final String key, final Object value) {
        attributes.put(key, value);
    }

    public String getId() {
        return id;
    }
}
