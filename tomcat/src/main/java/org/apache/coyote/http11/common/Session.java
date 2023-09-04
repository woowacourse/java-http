package org.apache.coyote.http11.common;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session implements HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    
    public Session() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("unsupported");
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }

    @Override
    public Object getValue(String name) {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException("unsupported");
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    @Override
    public void removeValue(String name) {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException("unsupported");
    }
}
