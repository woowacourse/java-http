package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JSession implements HttpSession {

    public static final String COOKIE_NAME = "JSESSIONID";

    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();
    private final long creationTime = System.currentTimeMillis();

    public JSession(String id) {
        this.id = id;
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
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public void setMaxInactiveInterval(int i) {
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public Object getValue(String s) {
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
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void putValue(String s, Object o) {
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public void removeValue(String s) {
    }

    @Override
    public void invalidate() {
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
