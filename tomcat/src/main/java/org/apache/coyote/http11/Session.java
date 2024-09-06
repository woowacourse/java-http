package org.apache.coyote.http11;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.techcourse.model.User;

public class Session {
    private static final int DEFAULT_EXPIRATION_TIME_IN_SECONDS = 1800;
    private final String sessionId;
    private final LocalDateTime creationTime;
    private Map<String, Object> attributes = new HashMap<>();
    private LocalDateTime lastAccessTime;
    private long expirationTimeInSeconds = DEFAULT_EXPIRATION_TIME_IN_SECONDS;

    public Session(User user) {
        this.sessionId = UUID.randomUUID().toString();
        attributes.put("userAccount", user);
        this.creationTime = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
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
}
