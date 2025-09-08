package org.apache.coyote.http11;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session implements HttpSession {

    private final SessionManager sessionManager;

    private final String id;
    private final Map<String, Object> attributes = new HashMap<>();
    private final long creationTime;
    private boolean invalidated = false;

    private Session(SessionManager sessionManager, String id, long creationTime, boolean invalidated) {
        this.sessionManager = sessionManager;
        this.id = id;
        this.creationTime = creationTime;
        this.invalidated = invalidated;
    }

    public static Session create(SessionManager sessionManager) {
        final var id = UUID.randomUUID().toString();
        final var creationTime = System.currentTimeMillis();
        final var session = new Session(sessionManager, id, creationTime, false);
        sessionManager.add(session);
        System.out.println("session: " + session);
        System.out.println("sessionManager: " + sessionManager);
        return session;
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
    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    @Override
    public void invalidate() {
        this.invalidated = true;
        this.attributes.clear();
        sessionManager.remove(this);
    }

    @Override
    public long getCreationTime() {
        return creationTime;
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
    @Deprecated
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        return attributes.keySet().toArray(new String[0]);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
