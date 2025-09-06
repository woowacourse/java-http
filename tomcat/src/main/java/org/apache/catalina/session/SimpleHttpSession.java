package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleHttpSession implements HttpSession {

    private final String id;
    private final Map<String, Object> attributes;

    public SimpleHttpSession(final String id, final Map<String, Object> attributes) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(attributes, "attributes must not be null");
        this.id = id;
        this.attributes = new ConcurrentHashMap<>(attributes);
    }

    public static SimpleHttpSession ofGeneratedId() {
        return new SimpleHttpSession(
                UUID.randomUUID()
                        .toString(), new ConcurrentHashMap<>()
        );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    @Override
    public void invalidate() {
        attributes.clear();
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
    public void setMaxInactiveInterval(final int interval) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException();
    }

    @Override
    public jakarta.servlet.ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getValue(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putValue(final String name, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeValue(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException();
    }
}
