package org.apache.coyote.servlet.session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session2 {

    public static final String JSESSIONID = "JSESSIONID";
    public static final String USER_ATTRIBUTE = "user";
    private static final int VALIDITY_IN_SECONDS = 600;

    private final String id;
    private final Map<String, Object> values = new HashMap<>();
    private final LocalDateTime expirationTime;

    private Session2(String id) {
        this.id = id;
        this.expirationTime = LocalDateTime.now().plusSeconds(VALIDITY_IN_SECONDS);
    }

    public static Session2 of() {
        final var sessionId = UUID.randomUUID();
        return new Session2(sessionId.toString());
    }

    public boolean isExpired() {
        final var currentTime = LocalDateTime.now();
        return currentTime.isAfter(expirationTime);
    }

    public String getId() {
        return id;
    }

    public boolean hasAttribute(String key) {
        return values.containsKey(key);
    }

    public void setAttribute(String key, Object value) {
        values.put(key, value);
    }
}
