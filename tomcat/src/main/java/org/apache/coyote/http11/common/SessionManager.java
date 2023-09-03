package org.apache.coyote.http11.common;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager  {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static Session addSession(final Session session) {
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public static Session findSession(final String id){
        if(!SESSIONS.containsKey(id)) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public static void removeSession(final String id) {
        SESSIONS.remove(id);
    }
}
