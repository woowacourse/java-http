package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private static final int DEFAULT_MAX_INACTIVE_INTERVAL_SECOND = 30 * 60;

    private final String id;

    private final Map<String, Object> attributes;

    private final int maxInactiveInterval;

    private final long createdTime;

    private long lastAccessedTime;

    private boolean expired;

    protected Session(String id, long createdTime) {
        this.id = id;
        this.attributes = new HashMap<>();
        this.createdTime = createdTime;
        this.lastAccessedTime = createdTime;
        this.maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL_SECOND;
    }

    public void setAttribute(String name, Object value) {
        validateNotExpired();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attribute의 이름은 필수입니다.");
        }
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        validateNotExpired();
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        validateNotExpired();
        attributes.remove(name);
    }

    public boolean isValid() {
        if (expired) {
            return false;
        }
        if (getIdleSecond() >= maxInactiveInterval) {
            expire();
        }
        return !expired;
    }

    private int getIdleSecond() {
        long now = System.currentTimeMillis();
        long idleTime = now - lastAccessedTime;
        return (int) idleTime / 1000;
    }

    public void expire() {
        this.expired = true;
        attributes.clear();
    }

    public void access() {
        validateNotExpired();
        this.lastAccessedTime = System.currentTimeMillis();
    }

    public String getId() {
        validateNotExpired();
        return id;
    }

    private void validateNotExpired() {
        if (!isValid()) {
            throw new IllegalStateException("만료된 세션입니다.");
        }
    }
}
