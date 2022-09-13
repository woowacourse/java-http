package customservlet;

import customservlet.exception.InvalidSessionException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.http.Session;

public class SessionManager implements Manager {

    private static final SessionManager sessionManager = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return sessionManager;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.entrySet().stream()
                .filter(it -> it.getKey().equalsIgnoreCase(id))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(InvalidSessionException::new);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean isValid(final Session session) {
        return SESSIONS.entrySet().stream()
                .anyMatch(it -> it.getValue().isSame(session));
    }
}
