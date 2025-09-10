package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionImpl extends AbstractHttpSession {

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public HttpSessionImpl(ServletContext servletContext) {
        super(servletContext);
    }

    @Override    public Object getAttribute(String name) {
        checkValid();
        return attributes.get(name);
    }

    /**
     * 세션에 저장된 모든 속성의 키 목록 반환
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        checkValid();
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkValid();
        if (value == null) {
            removeAttribute(name);
        } else {
            attributes.put(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        checkValid();
        attributes.remove(name);
    }
}
