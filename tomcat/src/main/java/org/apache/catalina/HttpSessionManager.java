package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessionManager {

    private static final Map<UUID, HttpSession> HTTP_SESSIONS = new ConcurrentHashMap<>();

    public void add(final HttpSession httpSession) {
        HTTP_SESSIONS.put(httpSession.uuid(), httpSession);
    }

    public Optional<HttpSession> find(final UUID jSessionId) {
        return Optional.ofNullable(HTTP_SESSIONS.get(jSessionId));
    }
}
