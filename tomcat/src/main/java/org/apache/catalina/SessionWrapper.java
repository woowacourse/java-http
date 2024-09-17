package org.apache.catalina;

import jakarta.http.HttpSessionWrapper;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

public class SessionWrapper implements HttpSessionWrapper {

    private final Manager manager;

    public SessionWrapper(Manager manager) {
        this.manager = manager;
    }

    @Override
    public HttpSession getSession(boolean sessionCreateIfAbsent, String sessionId) throws IOException {
        HttpSession foundSession = manager.findSession(sessionId);

        if (foundSession == null && sessionCreateIfAbsent) {
            UUID newSessionId = UUID.randomUUID();
            HttpSession session = manager.createSession(newSessionId.toString());
            manager.add(session);
            return session;
        }

        return foundSession;
    }
}
