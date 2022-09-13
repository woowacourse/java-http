package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.model.User;

public class SessionManager implements Manager {

    private final Map<User, String> sessions;

    private SessionManager(final Map<User, String> sessions) {
        this.sessions = sessions;
    }

    public static SessionManager init() {
        return new SessionManager(new ConcurrentHashMap<>());
    }

    @Override
    public void add(final User user, final HttpResponse httpResponse) {
        sessions.put(user, httpResponse.getSessionId());
    }

    @Override
    public void remove(final String sessionId) {
        final User user = sessions.entrySet().stream()
            .filter(session -> session.getValue().equals(sessionId))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 session ID 입니다. [%s]", sessionId)));

        sessions.remove(user);
    }

    public boolean isLoginAccount(final HttpRequest httpRequest) {
        if (httpRequest.doesNotContainSessionId()) {
            return false;
        }
        return sessions.entrySet().stream()
            .anyMatch(session -> session.getValue().equals(httpRequest.getSessionId()));
    }
}
