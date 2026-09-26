package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class Session implements HttpSession {

    private final String id;
    private final Manager manager;
    private final long creationTime;
    private final Map<String, Object> values = new HashMap<>();

    private long lastAccessedTime;
    private int maxInactiveInterval;
    private boolean isNew;
    private boolean valid;

    public Session(final String id, final Manager manager) {
        this.id = id;
        this.manager = manager;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
        this.isNew = true;
        this.valid = true;
    }

    public void access() {
        checkValid();

        lastAccessedTime = System.currentTimeMillis();
        isNew = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttribute(final String name) {
        checkValid();
        return values.get(name);
    }

    @Override
    public void setAttribute(
            final String name,
            final Object value
    ) {
        checkValid();

        if (value == null) {
            removeAttribute(name);
            return;
        }

        values.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        checkValid();
        values.remove(name);
    }

    @Override
    public void invalidate() {
        checkValid();

        values.clear();
        valid = false;
        manager.remove(this);
    }

    @Override
    public long getCreationTime() {
        checkValid();
        return creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        checkValid();
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException(
                "ServletContext는 지원하지 않습니다."
        );
    }

    @Override
    public void setMaxInactiveInterval(
            final int interval
    ) {
        checkValid();
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        checkValid();
        return maxInactiveInterval;
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException(
                "HttpSessionContext는 지원하지 않습니다."
        );

    }

    @Override
    @Deprecated
    public Object getValue(final String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkValid();

        return Collections.enumeration(
                values.keySet()
        );
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        checkValid();

        return values.keySet()
                .toArray(String[]::new);
    }

    @Override
    @Deprecated
    public void putValue(
            final String name,
            final Object value
    ) {
        setAttribute(name, value);
    }

    @Override
    @Deprecated
    public void removeValue(final String name) {
        removeAttribute(name);
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isNew() {
        checkValid();
        return isNew;
    }

    private void checkValid() {
        if (!valid) {
            throw new IllegalStateException(
                    "Session is already invalidated."
            );
        }
    }
}