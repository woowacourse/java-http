package org.apache.catalina.session;

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
    private final long creationTime = System.currentTimeMillis();

    public Session(final String id) {
        this.id = id;
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
        values.put(name, value);
    }

    @Override
    public void removeAttribute(final String name) {
        values.remove(name);
    }

    @Override
    public void invalidate() {
        values.clear();
        SessionManager.getInstance().remove(this);
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(values.keySet());
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException("아직 지원하지 않는 기능입니다.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("아직 지원하지 않는 기능입니다.");
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        throw new UnsupportedOperationException("아직 지원하지 않는 기능입니다.");
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("아직 지원하지 않는 기능입니다.");
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException("아직 지원하지 않는 기능입니다.");
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("더 이상 사용하지 않는 기능입니다.");
    }

    @Override
    public Object getValue(final String name) {
        throw new UnsupportedOperationException("더 이상 사용하지 않는 기능입니다.");
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException("더 이상 사용하지 않는 기능입니다.");
    }

    @Override
    public void putValue(final String name, final Object value) {
        throw new UnsupportedOperationException("더 이상 사용하지 않는 기능입니다.");
    }

    @Override
    public void removeValue(final String name) {
        throw new UnsupportedOperationException("더 이상 사용하지 않는 기능입니다.");
    }
}
