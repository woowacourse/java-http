package org.apache.catalina;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session implements HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    private boolean valid = true;

    public Session(final String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized Object getAttribute(final String name) {
        checkValid();
        return values.get(Objects.requireNonNull(name));
    }

    @Override
    public synchronized void setAttribute(final String name, final Object value) {
        checkValid();
        Objects.requireNonNull(name);
        if (value == null) {
            values.remove(name);
        } else {
            values.put(name, value);
        }
    }

    @Override
    public synchronized void removeAttribute(final String name) {
        checkValid();
        values.remove(Objects.requireNonNull(name));
    }

    @Override
    public synchronized void invalidate() {
        checkValid();
        valid = false;
        values.clear();
    }

    public synchronized boolean isValid() {
        return valid;
    }

    private void checkValid() {
        if (!valid) {
            throw new IllegalStateException("Session has been invalidated");
        }
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("ServletContext is not supported yet");
    }

    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("HttpSessionContext is not supported");
    }

    @Deprecated
    @Override
    public Object getValue(String name) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void removeValue(String name) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException();
    }
}
