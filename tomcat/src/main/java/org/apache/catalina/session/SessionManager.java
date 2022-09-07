package org.apache.catalina.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.user.User;
import org.apache.catalina.Manager;
import org.apache.catalina.session.exception.InvalidSessionIdException;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.http11response.ResponseComponent;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager sessionManager = new SessionManager();
    private static final String USER_KEY = "user";
    private static final String NEW_KEY = "new";
    private static final String JSESSIONID = "JSESSIONID";

    private SessionManager() {}

    public static SessionManager connect() {
        return sessionManager;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws InvalidSessionIdException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public Session generateSession(User user) {
        Session session = new Session();
        session.setAttribute(USER_KEY, user);
        return session;
    }

    public void addSessionIfAbsent(HttpCookie httpCookie) {
        if (!httpCookie.hasAttribute(JSESSIONID)) {
            Session session = new Session();
            session.setAttribute(NEW_KEY, true);
            add(session);
            httpCookie.setAttribute(JSESSIONID, session.getId());
        }
    }

    public void notifySessionIfNew(HttpCookie httpCookie, ResponseComponent responseComponent) {
        String jsessionId = httpCookie.getAttribute(JSESSIONID);
        Session session = findSession(jsessionId);
        if ((boolean) session.getAttribute(NEW_KEY)) {
            responseComponent.setCookie(httpCookie.toString());
            session.setAttribute(NEW_KEY, false);
        }
    }
}
