package org.apache.coyote.servlet.session;

import java.time.LocalDateTime;
import nextstep.jwp.model.User;

public class Session {

    public static final String JSESSIONID = "JSESSIONID";
    public static final int VALIDITY_IN_SECONDS = 600;

    private final User user;
    private final LocalDateTime expirationTime;

    private Session(User user, LocalDateTime expirationTime) {
        this.user = user;
        this.expirationTime = expirationTime;
    }

    public static Session of(User user) {
        final var expirationTime = LocalDateTime.now().plusSeconds(VALIDITY_IN_SECONDS);
        return new Session(user, expirationTime);
    }

    public boolean isExpired() {
        final var currentTime = LocalDateTime.now();
        return currentTime.isAfter(expirationTime);
    }
}
