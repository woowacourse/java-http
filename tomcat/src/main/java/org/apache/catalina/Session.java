package org.apache.catalina;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Session implements HttpSession {

    private final String id;
    private final long creationTime;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    private volatile long lastAccessedTime;
    private volatile int maxInactiveInterval;
    private volatile boolean isNew = true;

    public Session(final String id) {
        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
    }

    void access() {
        lastAccessedTime = System.currentTimeMillis();
        isNew = false;
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
        return values.get(name);
    }

    @Override
    @Deprecated
    public Object getValue(final String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(values.keySet());
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        return values.keySet().toArray(String[]::new);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    @Override
    @Deprecated
    public void putValue(final String name, final Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        values.remove(name);
    }

    @Override
    @Deprecated
    public void removeValue(final String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        values.clear();
        SessionManager.getInstance().remove(this);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
