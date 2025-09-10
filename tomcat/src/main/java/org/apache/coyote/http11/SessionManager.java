package org.apache.coyote.http11;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> sessionMap = new HashMap<>();

    public void addSession(String sessionId, Session session){
        sessionMap.put(sessionId, session);
    }

    public Session findSession(String sessionId){
        return sessionMap.get(sessionId);
    }

    public SessionManager() {}

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    public void remove(String sessionId){
        sessionMap.remove(sessionId);
    }
}
