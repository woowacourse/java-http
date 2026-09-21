package org.qupring.session;

import com.techcourse.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.request.HttpRequest;

public class SessionManager {

    private static final SessionManager INSTANCE =
            new SessionManager();

    private final Map<String, Session> SESSIONS =
            new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session createSession(
            HttpRequest request,
            User user
    ) {
        Session session = request.getSession(true);
        session.setAttribute("user", user);

        SessionManager.getInstance().add(session);
        return session;
    }

    public Session findSession(String id) {
        if (id == null) {
            return null;
        }

        return SESSIONS.get(id);
    }

    public void remove(String id) {
        if (id != null) {
            SESSIONS.remove(id);
        }
    }

}
