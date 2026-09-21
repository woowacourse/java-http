package org.apache.catalina;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session implements HttpSession {

    private final String id;
    private final Manager manager;
    private final long creationTime;
    private final Map<String, Object> values = new HashMap<>();

    private long lastAccessedTime;
    private int maxInactiveInterval;
    private boolean valid = true;
    private boolean isNew = true;

    public Session(final String id) {
        this(id, SessionManager.getInstance());
    }

    Session(final String id, final Manager manager) {
        this.id = Objects.requireNonNull(id);
        this.manager = Objects.requireNonNull(manager);
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
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
    public void setMaxInactiveInterval(final int interval) {
        maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(final String name) {
        validate();
        return values.get(name);
    }

    @Override
    @Deprecated
    public Object getValue(final String name) {
        return getAttribute(name);
    }

    @Override
    public java.util.Enumeration<String> getAttributeNames() {
        validate();
        return Collections.enumeration(values.keySet());
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        validate();
        return values.keySet().toArray(String[]::new);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        validate();
        Objects.requireNonNull(name);
        if (value == null) {
            removeAttribute(name);
            return;
        }
        values.put(name, value);
    }

    @Override
    @Deprecated
    public void putValue(final String name, final Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        validate();
        values.remove(name);
    }

    @Override
    @Deprecated
    public void removeValue(final String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        validate();
        values.clear();
        manager.remove(this);
        valid = false;
    }

    @Override
    public boolean isNew() {
        validate();
        return isNew;
    }

    void access() {
        validate();
        lastAccessedTime = System.currentTimeMillis();
        isNew = false;
    }

    private void validate() {
        if (!valid) {
            throw new IllegalStateException("Session has already been invalidated");
        }
    }
}
