package org.apache.coyote.http11;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleSession implements HttpSession {

    private static final int DEFAULT_MAX_INACTIVE_INTERVAL = 30 * 60;

    private final String id;
    private final long creationTime;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    private volatile long lastAccessedTime;
    private volatile int maxInactiveInterval;
    private volatile boolean invalidated;
    private volatile boolean newSession;

    private SimpleSession(
            String id,
            long creationTime,
            long lastAccessedTime,
            int maxInactiveInterval
    ) {
        this.id = id;
        this.creationTime = creationTime;
        this.lastAccessedTime = lastAccessedTime;
        this.maxInactiveInterval = maxInactiveInterval;
        this.invalidated = false;
        this.newSession = true;
    }

    public static SimpleSession create() {
        long now = System.currentTimeMillis();

        return new SimpleSession(
                UUID.randomUUID().toString(),
                now,
                now,
                DEFAULT_MAX_INACTIVE_INTERVAL
        );
    }

    public void access() {
        validate();
        lastAccessedTime = System.currentTimeMillis();
    }

    public void markEstablished() {
        newSession = false;
    }

    public boolean isExpired() {
        if (maxInactiveInterval < 0) {
            return false;
        }

        long inactiveMillis = System.currentTimeMillis() - lastAccessedTime;

        return inactiveMillis >= maxInactiveInterval * 1000L;
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    private void validate() {
        if (invalidated) {
            throw new IllegalStateException("이미 무효화된 세션입니다.");
        }
    }

    @Override
    public long getCreationTime() {
        validate();
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        validate();
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        validate();
        maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        validate();
        return maxInactiveInterval;
    }

    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        validate();
        return attributes.get(name);
    }

    @Deprecated
    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        validate();
        return Collections.enumeration(attributes.keySet());
    }

    @Deprecated
    @Override
    public String[] getValueNames() {
        validate();

        return attributes.keySet()
                .toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        validate();

        if (value == null) {
            removeAttribute(name);
            return;
        }

        attributes.put(name, value);
    }

    @Deprecated
    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        validate();
        attributes.remove(name);
    }

    @Deprecated
    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public synchronized void invalidate() {
        validate();

        invalidated = true;
        attributes.clear();
    }

    @Override
    public boolean isNew() {
        validate();
        return newSession;
    }
}
