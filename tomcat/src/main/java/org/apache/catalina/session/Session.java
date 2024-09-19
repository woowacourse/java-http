package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private static final String JSESSIONID = "JSESSIONID=";
    private static final String SPACE = " ";

    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String toHeader(String uuid) {
        return JSESSIONID + uuid + SPACE;
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(final String name) {
        return attributes.get(name);
    }
}
