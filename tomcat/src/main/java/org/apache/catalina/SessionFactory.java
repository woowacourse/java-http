package org.apache.catalina;

import java.util.UUID;

public class SessionFactory {

    public static Session create() {
        final Session session = new Session(UUID.randomUUID().toString());
        final SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);

        return session;
    }
}
