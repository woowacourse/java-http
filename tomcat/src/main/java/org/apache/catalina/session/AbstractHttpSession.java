package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractHttpSession implements HttpSession {

    private static final long MILLIS_PER_SECOND = 1000L;

    protected final String id;
    protected final long creationTime;
    protected AtomicLong lastAccessedTime;
    protected int maxInactiveInterval;
    protected final AtomicBoolean isNew = new AtomicBoolean(true);
    protected final AtomicBoolean valid = new AtomicBoolean(true);

    protected final ServletContext servletContext;

    protected AbstractHttpSession(ServletContext servletContext) {
        this.id = UUID.randomUUID().toString();
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = new AtomicLong(this.creationTime);
        this.servletContext = servletContext;
    }

    /**
     * 세션의 고유 식별자(UUID)
     */
    @Override
    public String getId() { return id; }

    /**
     * 세션이 만들어진 시각 반환
     */
    @Override
    public long getCreationTime() { checkValid(); return creationTime; }

    /**
     * 마지막 요청 시각 반환
     */
    @Override
    public long getLastAccessedTime() { checkValid(); return lastAccessedTime.get(); }

    /**
     * 세션에 접근(access)했음을 기록하는 메서드.
     * why? 세션 만료 처리 기준이 lastAccessedTime 기반이므로, 요청이 들어올 때마다 이 값을 갱신해야 세션 유지가 가능
     */
    public void access() {
        lastAccessedTime.set(System.currentTimeMillis());
        isNew.set(false);
    }

    /**
     * 이 세션이 속한 웹 애플리케이션의 전역 컨텍스트(ServletContext)를 반환.
     * 어떤 세션이 어느 애플리케이션(ServletContext)에 속해 있는지 확인할 때
     */
    @Override
    public ServletContext getServletContext() { return servletContext; }

    /**
     * 세션 만료 기준 시간을 설정(초 단위)
     */
    @Override
    public void setMaxInactiveInterval(int interval) { this.maxInactiveInterval = interval; }

    /**
     * 현재 설정된 세션 유효 시간 반환
     */
    @Override
    public int getMaxInactiveInterval() { return maxInactiveInterval; }

    /**
     * 세션 강제로 무효화
     */
    @Override
    public void invalidate() { valid.set(false); }

    /**
     * 세션이 새로 생성된 것인지 여부
     */
    @Override
    public boolean isNew() { checkValid(); return isNew.get(); }

    public void checkValid() {
        if (!valid.get() || isExpired()) {
            throw new IllegalStateException("세션이 이미 만료되었거나 무효화되었습니다.");
        }
    }

    public boolean isExpired() {
        if (maxInactiveInterval <= 0) {
            return false;
        }
        long inactiveDuration = System.currentTimeMillis() - lastAccessedTime.get();
        long maxInactiveMillis = maxInactiveInterval * MILLIS_PER_SECOND;
        return inactiveDuration > maxInactiveMillis;
    }

    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() { return null; }

    @Deprecated
    @Override
    public Object getValue(String name) { return getAttribute(name); }

    @Deprecated
    @Override
    public String[] getValueNames() {
        List<String> names = Collections.list(getAttributeNames());
        return names.toArray(new String[0]);
    }

    @Deprecated
    @Override
    public void putValue(String name, Object value) { setAttribute(name, value); }

    @Deprecated
    @Override
    public void removeValue(String name) { removeAttribute(name); }
}
