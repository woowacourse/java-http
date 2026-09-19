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


    public Session(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        values.put(name, value);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }

    public void invalidate() {
    }

    /*
    2026.08.19
    아래 기능은 우테코 Tomcat 구현하기에서 활용되지 않는 기능이다.
     */

    @Deprecated
    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public void setMaxInactiveInterval(int interval) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public Object getValue(String name) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public void putValue(String name, Object value) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public void removeValue(String name) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    @Deprecated
    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }
}
