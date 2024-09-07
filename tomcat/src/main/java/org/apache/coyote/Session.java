package org.apache.coyote;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session implements HttpSession {

    private final String id;
    private final long creationTime;
    private long lastAccessedTime;
    private final Map<String, Object> values = new ConcurrentHashMap<>();
    private int maxInactiveInterval;
    private boolean isNew;

    private Session(String id) {
        this.id = id;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
        isNew = true;
    }

    public static Session createRandomSession() {
        return new Session(UUID.randomUUID().toString());
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
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
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
    public Object getAttribute(String name) {
        updateLastAccessedTime();
        return values.get(name);
    }

    @Override
    @Deprecated
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        updateLastAccessedTime();
        return Collections.enumeration(values.keySet());
    }

    @Override
    public String[] getValueNames() {
        updateLastAccessedTime();
        return values.keySet().toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        updateLastAccessedTime();
        values.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);  // Alias for setAttribute
    }

    @Override
    public void removeAttribute(String name) {
        updateLastAccessedTime();
        values.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        values.clear();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    private void updateLastAccessedTime() {
        this.lastAccessedTime = System.currentTimeMillis();
    }
}
