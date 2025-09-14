package org.apache.catalina.session;

import com.java.http.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleSession implements Session {

    // 동시 접근의 유스케이스가 존재하지 않으므로 기본 컬렉션을 사용한다.
    private final Map<String, Object> attributes = new HashMap<>();

    private String id = createId();

    @Override
    public String id() {
        return id;
    }

    @Override
    public Object attributeOf(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void changeSessionId() {
        id = createId();
    }

    private static String createId() {
        return UUID.randomUUID().toString();
    }
}
