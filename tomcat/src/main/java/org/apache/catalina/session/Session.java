package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
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
    public String getId() {
        return this.id;
    }

    @Override
    public Object getAttribute(final String name) {
        if (!values.containsKey(name)) {
            return null;
        }
        return values.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(values.keySet());
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
        SessionManager.INSTANCE.remove(this);
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("지원하지 않는 메서드입니다: getMaxInactiveInterval");
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        throw new UnsupportedOperationException("지원하지 않는 메서드입니다: setMaxInactiveInterval");
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException("지원하지 않는 메서드입니다: isNew");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("지원하지 않는 메서드입니다: getServletContext");
    }

    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException("지원하지 않는 메서드입니다: getCreationTime");
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException("지원하지 않는 메서드입니다: getLastAccessedTime");
    }
}
