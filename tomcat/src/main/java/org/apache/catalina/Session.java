package org.apache.catalina;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session implements HttpSession {

    private final String id;
    private final Map<String, Object> values = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
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
    public Object getAttribute(final String name) {
        return values.get(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        if (value == null) {
            removeAttribute(name);
            return;
        }
        values.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        values.remove(name);
    }

    @Override
    public void invalidate() {
        values.clear();
        SessionManager.getInstance().remove(id);
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void putValue(String name, Object value) {

    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
