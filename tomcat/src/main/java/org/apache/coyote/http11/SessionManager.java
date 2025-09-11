package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public static Optional<Session> findSession(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public static void removeSession(String id) {
        sessions.remove(id);
    }

    public static Session resolveSession(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String jsessionid = httpRequest.getCookies().getCookie("JSESSIONID");

        return Optional.ofNullable(jsessionid)
                .flatMap(SessionManager::findSession)
                .orElseGet(() -> {
                    Session newSession = createSession();
                    httpResponse.addCookie(Cookie.ofJSessionId(newSession.getId()));
                    return newSession;
                });
    }
}
