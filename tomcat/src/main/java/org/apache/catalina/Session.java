package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 클라이언트별 세션 데이터 관리
public class Session {

    // id = uuid (JSESSION)
    private final String id;

    // key = 어떤 데이터인지, value = 실제 데이터
    private final Map<String, Object> values = new HashMap<>();

    private Session(final String id) {
        this.id = id;
    }

    public static Session create() {
        return new Session(UUID.randomUUID().toString());
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

    public void invalidate() {
        values.clear();
    }
}
