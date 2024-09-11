package com.techcourse.session;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

public class CustomSession implements HttpSession {

    private static final AtomicLong sessionIdIndex = new AtomicLong();

    private final Map<String, Object> attributes;
    private final String id;

    public CustomSession() {
        this.id = String.valueOf(sessionIdIndex.incrementAndGet());
        this.attributes = new HashMap<>();
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
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public void setMaxInactiveInterval(final int i) {
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(final String s) {
        if (!attributes.containsKey(s)) {
            throw new IllegalArgumentException("존재하지 않는 Session Attribute");
        }
        return attributes.get(s);
    }

    @Override
    public Object getValue(final String s) {
        if (!attributes.containsKey(s)) {
            throw new IllegalArgumentException("존재하지 않는 Session Attribute");
        }
        return attributes.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> names = new HashSet<>(attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public String[] getValueNames() {
        return attributes.keySet()
                .toArray(new String[0]);
    }

    @Override
    public void setAttribute(final String s, final Object o) {
        attributes.put(s, o);
    }

    @Override
    public void putValue(final String s, final Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(final String s) {
        attributes.remove(s);
    }

    @Override
    public void removeValue(final String s) {
        attributes.put(s, "");
    }

    @Override
    public void invalidate() {
        attributes.clear();
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
