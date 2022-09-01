package org.apache.coyote.http11;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Session implements HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

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
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        values.put("maxInactiveInterval", interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return (int) values.get("maxInactiveInterval");
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(final String name) {
        return values.get(name);
    }

    @Override
    public Object getValue(final String name) {
        return values.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return (Enumeration<String>) values.keySet();
    }

    @Override
    public String[] getValueNames() {
        return values.keySet().toArray(new String[0]);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    @Override
    public void putValue(final String name, final Object value) {
        values.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        values.remove(name);
    }

    @Override
    public void removeValue(final String name) {
        values.remove(name);
    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
