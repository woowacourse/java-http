package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.LoginManager;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionManager;

import java.util.Map;

public abstract class LoginFilter {

    private static final LoginManager loginManager = new SessionManager();

    protected boolean isAlreadyLogined(final String jSessionId) {
        return loginManager.isAlreadyLogined(jSessionId);
    }

    protected void setSession(final String jSessionId, final Map<String, String> sessionData) {
        final Session session = new Session(jSessionId);
        for (final String key : sessionData.keySet()) {
            session.add(key, sessionData.get(key));
        }
        loginManager.add(session);
    }
}
