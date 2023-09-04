package org.apache.coyote.http11.request;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Session {

    private final String id;
    private final Map<String, Object> value = new HashMap<>();
    private LocalDateTime expiredAt;

    public Session(final String id) {
        this.id = id;
        this.expiredAt = LocalDateTime.now().plusDays(1);
    }

    public Session(final String id,
                   final LocalDateTime expiredAt) {
        this.id = id;
        this.expiredAt = expiredAt;
    }

    public Object getAttribute(final String name) {
        validate();
        return value.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        validate();
        this.value.put(name, value);
    }

    public void removeAttribute(final String name) {
        value.remove(name);
    }

    public void invalidate() {
        expiredAt = LocalDateTime.now();
    }

    private void validate(){
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("유효기간이 지난 세션입니다");
        }
    }

    public String getId() {
        return id;
    }
}
