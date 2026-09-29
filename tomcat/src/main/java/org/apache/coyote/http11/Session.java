package org.apache.coyote.http11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.error.HttpException;

public class Session {

    private final String id;
    private final Map<String, Object> values;

    private Session(final String id) {
        this.id = id;
        this.values = new ConcurrentHashMap<>();
    }

    public static Session init(final String id) {
        return new Session(id);
    }

    public String id() {
        return id;
    }

    public Object getAttribute(final String name) {
        return values.getOrDefault(name, null);
    }

    public void addAttribute(final String name, final Object value) {
        if (name == null || value == null) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다.");
        }
        values.put(name, value);
    }

    public boolean hasAttribute(final String name) {
        return values.containsKey(name);
    }

    public void removeAttribute(final String name) {
        values.remove(name);
    }
}
