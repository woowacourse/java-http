package org.apache.coyote.http11;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session implements HttpSession {

    private final String id;
    private final long creationTime;
    private final Map<String, Object> values = new ConcurrentHashMap<>();
    private volatile long lastAccessedTime;
    private volatile int maxInactiveInterval;
    private volatile boolean valid = true;

    public Session(final String id) {
        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
    }

    @Override
    public long getCreationTime() {
        ensureValid();
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        ensureValid();
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(final String name) {
        ensureValid();
        lastAccessedTime = System.currentTimeMillis();
        return values.get(name);
    }

    @Override
    public Object getValue(final String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        ensureValid();
        return Collections.enumeration(values.keySet());
    }

    @Override
    public String[] getValueNames() {
        ensureValid();
        return values.keySet().toArray(String[]::new);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        ensureValid();
        lastAccessedTime = System.currentTimeMillis();
        if (value == null) {
            values.remove(name);
            return;
        }
        values.put(name, value);
    }

    @Override
    public void putValue(final String name, final Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        ensureValid();
        lastAccessedTime = System.currentTimeMillis();
        values.remove(name);
    }

    @Override
    public void removeValue(final String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        ensureValid();
        values.clear();
        valid = false;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    private void ensureValid() {
        if (!valid) {
            throw new IllegalStateException("Session is invalidated");
        }
    }
}
