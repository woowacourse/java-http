package org.apache.catalina.session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    private static final int DEFAULT_EXPIRATION_TIME_IN_SECONDS = 1800;
    private final String sessionId;
    private final LocalDateTime creationTime;
    private Map<String, Object> attributes = new HashMap<>();
    private LocalDateTime lastAccessTime;
    private long expirationTimeInSeconds = DEFAULT_EXPIRATION_TIME_IN_SECONDS;

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
        this.creationTime = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
    }

    public void setAttributes(String key, Object value) {
        attributes.put(key, value);
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return lastAccessTime.plusSeconds(expirationTimeInSeconds).isBefore(now);
    }

    public void updateLastAccessTime() {
        this.lastAccessTime = LocalDateTime.now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isNew(){
        return attributes.isEmpty();
    }
}
