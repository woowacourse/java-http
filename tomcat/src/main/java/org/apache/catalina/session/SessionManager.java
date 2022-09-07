package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.user.User;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.http11handler.exception.CookieNotFoundException;
import org.apache.coyote.http11.http11request.Http11Request;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager sessionManager = new SessionManager();
    private static final String USER_KEY = "user";

    private SessionManager() {}

    public static SessionManager connect() {
        return sessionManager;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws IOException {
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

    public void assignSessionIfAbsent(Http11Request http11Request) {
        try {
            HttpCookie httpCookie = http11Request.getCookie();
            if (!httpCookie.hasJessionId()) {
                assignSession(http11Request, httpCookie);
            }
        } catch (CookieNotFoundException e) {
            System.out.println("nocookie@@@@@@@");
            assignCookieWithSession(http11Request);
        }
    }

    private void assignCookieWithSession(Http11Request http11Request) {
        HttpCookie httpCookie = new HttpCookie();
        assignSession(http11Request, httpCookie);
    }

    private void assignSession(Http11Request http11Request, HttpCookie httpCookie) {
        Session session = new Session();
        add(session);
        httpCookie.setJsessionId(session.getId());
        http11Request.setCookie(httpCookie);
    }
}
