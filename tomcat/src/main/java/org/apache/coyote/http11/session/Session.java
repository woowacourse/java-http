package org.apache.coyote.http11.session;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    public static final int DEFAULT_EXPIRED_MINUTES = 30;

    private final String id;
    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();
    private final LocalDateTime expiredTime;

    private Session(final String id, final LocalDateTime expiredTime) {
        this.id = id;
        this.expiredTime = expiredTime;
    }

    public Session() {
        this(UUID.randomUUID().toString(), LocalDateTime.now().plusMinutes(DEFAULT_EXPIRED_MINUTES));
    }

    public Session(final long timeOutMillis) {
        this(UUID.randomUUID().toString(), LocalDateTime.now().plusNanos(timeOutMillis * 1_000_000));
    }

    public String getId() {
        return this.id;
    }

    public Optional<Object> getAttribute(final String name) {
        return Optional.ofNullable(attributes.get(name));
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    public boolean isExpired() {
        return expiredTime.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Session)) {
            return false;
        }
        final Session session = (Session) o;
        return Objects.equals(getId(), session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
