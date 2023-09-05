package org.apache.coyote.http11;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Session implements HttpSession {

    private final Map<String, Object> values = new HashMap<>();
    private final String id;

    public Session(String id) {
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
    public Object getAttribute(String name) {
        return values.get(name);
    }

    @Override
    public Object getValue(String name) {
        return values.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(values.keySet());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        values.remove(name);
    }

    @Override
    public void removeValue(String name) {
        values.remove(name);
    }

    @Override
    public void invalidate() {
        values.clear();
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
