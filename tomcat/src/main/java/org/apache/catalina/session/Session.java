package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class Session implements HttpSession {
    private static final String UNSUPPORTED_MESSAGE = "지원하지 않는 기능입니다.";
    private static final int DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS = 30 * 60;
    private static final long MILLIS_PER_SECOND = 1000L;

    private final String id;
    private final Map<String, Object> attributes;
    private final long creationTime;
    private volatile long lastAccessedTime;
    private volatile int maxInactiveInterval;

    Session(String id, long now) {
        this.id = id;
        this.attributes = new ConcurrentHashMap<>();
        this.creationTime = now;
        this.lastAccessedTime = now;
        this.maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;
    }

    static Session create(long now) {
        return new Session(UUID.randomUUID().toString(), now);
    }

    void access(long now) {
        this.lastAccessedTime = now;
    }

    boolean isExpired(long now) {
        // 서블릿 규약: 0 이하면 만료되지 않는다
        if (maxInactiveInterval <= 0) {
            return false;
        }
        return now - lastAccessedTime >= maxInactiveInterval * MILLIS_PER_SECOND;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value == null) {
            attributes.remove(name);
            return;
        }
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    @Deprecated
    public Object getValue(String name) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    @Deprecated
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    @Deprecated
    public void removeValue(String name) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }
}
