package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

     public void addSession(final Session session) {
         SESSIONS.put(session.getId(), session);
     }

     public Session findSession(final String sessionId) {
         return SESSIONS.get(sessionId);
     }

     public void removeSession(final String sessionId) {
         SESSIONS.remove(sessionId);
     }
}
