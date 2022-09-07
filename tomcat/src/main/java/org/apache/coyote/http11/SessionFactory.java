package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.model.User;

public class SessionFactory {

    private final Map<User, String> sessions;

    private SessionFactory(final Map<User, String> sessions) {
        this.sessions = sessions;
    }

    public static SessionFactory init() {
        return new SessionFactory(new HashMap<>());
    }

    public void add(final User user, final HttpResponse httpResponse) {
        sessions.put(user, httpResponse.getSessionId());
    }

    public boolean isLoginAccount(final HttpRequest httpRequest) {
        if (httpRequest.doesNotHaveSessionId()) {
            return false;
        }
        return sessions.entrySet().stream()
            .anyMatch(session -> session.getValue().equals(httpRequest.getSessionId()));
    }
}
