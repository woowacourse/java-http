package org.apache.coyote.http.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http.HeaderKey;
import org.apache.coyote.http.response.HttpResponse;

public class SessionManager {

    private SessionManager() {
    }

    private static final Map<String, Session> sessions = new HashMap<>();

    public static Session findSession(String sessionId) {
        return sessions.getOrDefault(sessionId, new Session());
    }

    public static void manageSession(HttpResponse httpResponse, Session session) {
        if (httpResponse.contains(HeaderKey.SET_COOKIE.value)) {
            sessions.put(session.getId(), session);
        }
    }
}
