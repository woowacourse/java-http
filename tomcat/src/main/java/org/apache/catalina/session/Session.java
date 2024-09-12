package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class Session {

    private static final int DEFAULT_MAX_INACTIVE_INTERVAL_SECOND = 30 * 60;

    private final String id;

    private final Map<String, Object> attributes;

    private final Manager manager;

    private final int maxInactiveInterval;

    private long createTime;

    private long lastAccessedTime;

    protected Session(String id, Manager manager) {
        this.id = id;
        this.attributes = new HashMap<>();
        this.manager = manager;
        this.manager.add(this);
        this.maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL_SECOND;
    }

    public void setAttribute(String name, Object value) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Attribute의 이름은 필수입니다.");
        }
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public boolean isValid() {
        if (getIdleSecond() >= maxInactiveInterval) {
            expire();
            return false;
        }
        return true;
    }

    private int getIdleSecond() {
        long now = System.currentTimeMillis();
        long idleTime = now - lastAccessedTime;
        return (int) idleTime / 1000;
    }

    private void expire() {
        manager.remove(this);
    }

    public void access() {
        this.lastAccessedTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setCreateTime(long time) {
        this.createTime = time;
        this.lastAccessedTime = time;
    }
}
