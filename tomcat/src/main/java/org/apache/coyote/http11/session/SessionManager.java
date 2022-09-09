package org.apache.coyote.http11.session;

import java.util.Map;
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

    public Session getSession(HttpRequest httpRequest) {
        Cookie cookie = httpRequest.getCookie();
        if (cookie == null) {
            return null;
        }
        String jSessionId = (String) cookie.getJSessionId();

        if (jSessionId == null) {
            return null;
        }
        return findSession(jSessionId);
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
