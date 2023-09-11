package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {


    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();
    private boolean isFirstSent;


    public Session() {
        this(UUID.randomUUID().toString());
    }

    private Session(final String id) {
        this.id = id;
        this.isFirstSent = true;
    }

    public boolean isFirstSent() {
        return isFirstSent;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.getOrDefault(name, null);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void setFirstSent(final boolean isFirstSent) {
        this.isFirstSent = isFirstSent;
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        SessionManager.removeSession(id);
    }

}
