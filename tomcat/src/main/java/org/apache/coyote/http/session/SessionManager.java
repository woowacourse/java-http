package org.apache.coyote.http.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http.HttpHeader.HEADER_KEY;
import org.apache.coyote.http.response.HttpResponse;

public class SessionManager {

    private SessionManager() {
    }

    private static final Map<String, Session> sessionManager = new HashMap<>();

    public static Session findSession(String sessionId) {
        return sessionManager.getOrDefault(sessionId, new Session());
    }

    public static void manageSession(HttpResponse httpResponse, Session session) {
        if (httpResponse.contains(HEADER_KEY.SET_COOKIE.value)) {
            sessionManager.put(session.getJsessionId(), session);
        }
    }
}
