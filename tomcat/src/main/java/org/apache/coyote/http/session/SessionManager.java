package org.apache.coyote.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http.HeaderKey;
import org.apache.coyote.http.response.HttpResponse;

public class SessionManager {

    private SessionManager() {
    }

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static Session findSession(String sessionId) {
        if (sessionId == null) {
            return new Session();
        }
        return sessions.get(sessionId);
    }

    public static void manageSession(HttpResponse httpResponse, Session session) {
        if (httpResponse.contains(HeaderKey.SET_COOKIE.value)) {
            sessions.put(session.getId(), session);
        }
    }
}
