package org.apache.coyote.servlet.session;

import java.time.LocalDateTime;

public class Session {

    public static final String JSESSIONID = "JSESSIONID";
    private static final int VALIDITY_IN_MINUTES = 10;

    private final Long userId;
    private final LocalDateTime expirationTime;

    private Session(Long userId, LocalDateTime expirationTime) {
        this.userId = userId;
        this.expirationTime = expirationTime;
    }

    public static Session of(Long userId) {
        final var expirationTime = LocalDateTime.now().plusMinutes(VALIDITY_IN_MINUTES);
        return new Session(userId, expirationTime);
    }
}
