package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingListener;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class Session implements HttpSession {

    private final String id;
    private final Manager manager;
    private final long creationTime;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private boolean valid = true;

    public Session(String id, Manager manager) {
        this.id = Objects.requireNonNull(id, "세션 ID는 null일 수 없습니다.");
        this.manager = Objects.requireNonNull(manager, "세션 관리자는 null일 수 없습니다.");
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public synchronized long getCreationTime() {
        ensureValid();
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized long getLastAccessedTime() {
        ensureValid();
        throw new UnsupportedOperationException("요청 접근 시각 추적은 지원하지 않습니다.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("서블릿 컨텍스트는 지원하지 않습니다.");
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        throw new UnsupportedOperationException("세션 시간 만료는 지원하지 않습니다.");
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException("세션 시간 만료는 지원하지 않습니다.");
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException("레거시 세션 컨텍스트는 지원하지 않습니다.");
    }

    @Override
    public synchronized Object getAttribute(String name) {
        ensureValid();
        return attributes.get(requireAttributeName(name));
    }

    @Override
    @Deprecated
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public synchronized Enumeration<String> getAttributeNames() {
        ensureValid();
        return Collections.enumeration(List.copyOf(attributes.keySet()));
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        return Collections.list(getAttributeNames()).toArray(String[]::new);
    }

    @Override
    public synchronized void setAttribute(String name, Object value) {
        ensureValid();
        requireAttributeName(name);
        if (value == null) {
            attributes.remove(name);
            return;
        }
        if (value instanceof HttpSessionBindingListener) {
            throw new UnsupportedOperationException("세션 속성 리스너는 지원하지 않습니다.");
        }
        attributes.put(name, value);
    }

    @Override
    @Deprecated
    public synchronized void putValue(String name, Object value) {
        ensureValid();
        Objects.requireNonNull(value, "속성 값은 null일 수 없습니다.");
        setAttribute(name, value);
    }

    @Override
    public synchronized void removeAttribute(String name) {
        ensureValid();
        attributes.remove(requireAttributeName(name));
    }

    @Override
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public synchronized void invalidate() {
        ensureValid();
        valid = false;
        attributes.clear();
        manager.remove(this);
    }

    @Override
    public synchronized boolean isNew() {
        ensureValid();
        throw new UnsupportedOperationException("클라이언트의 세션 참여 여부 추적은 지원하지 않습니다.");
    }

    private String requireAttributeName(String name) {
        return Objects.requireNonNull(name, "속성 이름은 null일 수 없습니다.");
    }

    private void ensureValid() {
        if (!valid) {
            throw new IllegalStateException("무효화된 세션입니다.");
        }
    }
}
