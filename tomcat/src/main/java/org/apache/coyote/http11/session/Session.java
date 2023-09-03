package org.apache.coyote.http11.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Session implements HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public Session(String id) {
        this.id = id;
    }

    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        return values.get(name);
    }

    @Override
    public Object getValue(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(values.keySet());
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) {
        values.remove(name);
    }

    @Override
    public void removeValue(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException();
    }
}
