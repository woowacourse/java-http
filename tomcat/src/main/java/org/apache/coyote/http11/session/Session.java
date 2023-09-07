package org.apache.coyote.http11.session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private static final int EXPIRED_MAX_MINUTE = 30;

    private final String id;
    private final LocalDateTime expirationTime;
    private final Map<String, Object> values = new HashMap<>();

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.expirationTime = LocalDateTime.now().plusMinutes(EXPIRED_MAX_MINUTE);
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
        values.clear();
    }

    public boolean isExpired() {
        return expirationTime.isBefore(LocalDateTime.now());
    }
}
