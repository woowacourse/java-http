package org.apache.coyote.http11.session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    private final LocalDateTime createdTime;
    private final int maxInactiveInterval;

    public Session(final LocalDateTime createdTime, final int maxInactiveInterval) {
        this(String.valueOf(UUID.randomUUID()), createdTime, maxInactiveInterval);

    }

    public Session(final String id, final LocalDateTime createdTime, final int maxInactiveInterval) {
        this.id = id;
        this.createdTime = createdTime;
        this.maxInactiveInterval = maxInactiveInterval;
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
        final LocalDateTime expiredTime = createdTime.plusSeconds(maxInactiveInterval);
        return time.isAfter(expiredTime) || time.isEqual(expiredTime);
    }
}
