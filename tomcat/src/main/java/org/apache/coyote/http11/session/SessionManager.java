package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Optional<Session> getSession(HttpRequest httpRequest) {
        Optional<Cookie> cookie = httpRequest.getCookie();
        if (cookie.isEmpty()) {
            return Optional.empty();
        }
        Optional<String> jSessionId = cookie.get().getJSessionId();

        if (jSessionId.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(findSession(jSessionId.get()));
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public void setUserSession(HttpResponse httpResponse, User user) {
        Session session = createSession(httpResponse);
        session.setUserAttribute(user);
        SESSIONS.put(session.getId(), session);
    }

    public Session createSession(HttpResponse httpResponse) {
        String id = UUID.randomUUID().toString();
        Session session = new Session(id);
        httpResponse.setCookie(Cookie.fromJSessionId(id));

        return session;
    }
}
