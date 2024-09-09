package org.apache.coyote.http11.session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values;
    private final LocalDateTime createdTime;
    private final LocalDateTime expiresTime;

    public Session(final LocalDateTime createdTime, final LocalDateTime expiresTime) {
        this(String.valueOf(UUID.randomUUID()), createdTime, expiresTime);

    }

    public Session(final String id, final LocalDateTime createdTime, final LocalDateTime expiresTime) {
        this.id = id;
        this.values = new HashMap<>();
        this.createdTime = createdTime;
        this.expiresTime = expiresTime;
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

    public boolean isExpired(final LocalDateTime time) {
        return time.isAfter(expiresTime);
    }
}
