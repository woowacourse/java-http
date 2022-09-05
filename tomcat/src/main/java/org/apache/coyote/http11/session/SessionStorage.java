package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.ToString;
import nextstep.jwp.model.User;

@ToString
public class SessionStorage {

    private static final Map<String, User> session = new HashMap<>();

    private SessionStorage() {
    }

    public static String add(final User user) {
        String sessionId = UUID.randomUUID().toString();
        session.put(sessionId, user);
        return sessionId;
    }

    public static Optional<User> findAccountBySessionId(final String sessionId) {
        return Optional.ofNullable(session.get(sessionId));
    }
}
