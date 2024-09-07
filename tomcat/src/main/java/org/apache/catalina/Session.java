package org.apache.catalina;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
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
        return 0;
    }

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

    public Object getAttribute(String key) {
        return values.get(key);
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

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public void putValue(String name, Object value) {

    }

    public void removeAttribute(String key) {
        values.remove(key);
    }

    @Override
    public void removeValue(String name) {

    }

    public void invalidate() {
        values.clear();
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
