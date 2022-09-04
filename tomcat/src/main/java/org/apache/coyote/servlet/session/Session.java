package org.apache.coyote.servlet.session;

import java.time.LocalDateTime;

public class Session {

    public static final String JSESSIONID = "JSESSIONID";
    public static final int VALIDITY_IN_SECONDS = 600;

    private final Long userId;
    private final LocalDateTime expirationTime;

    private Session(Long userId, LocalDateTime expirationTime) {
        this.userId = userId;
        this.expirationTime = expirationTime;
    }

    public static Session of(Long userId) {
        final var expirationTime = LocalDateTime.now().plusSeconds(VALIDITY_IN_SECONDS);
        return new Session(userId, expirationTime);
    }
}
