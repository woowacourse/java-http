package org.apache.catalina.session;

import com.techcourse.model.User;
import org.apache.coyote.http11.headers.HttpCookies;

public class SessionService {

    private static final String SESSION_ID = "JSESSIONID";
    private static final String SESSION_DELIMITER = "=";

    private final SessionManager sessionManager;


    public SessionService() {
        this.sessionManager = SessionManager.getInstance();
    }

    public String create(User user) {
        Session session = Session.create();
        session.setAttribute("user", user);
        sessionManager.add(session);
        return SESSION_ID + SESSION_DELIMITER + session.getId();
    }

    public boolean isLoggedIn(HttpCookies cookies) {
        try {
            String value = cookies.getByName(SESSION_ID);
            Session session = sessionManager.findSession(value);
            return session != null;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
