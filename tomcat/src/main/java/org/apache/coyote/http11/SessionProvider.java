package org.apache.coyote.http11;

import java.util.UUID;

public final class SessionProvider {

    public static Session provide() {
        final SessionManager sessionManager = SessionManager.getInstance();
        final UUID uuid = UUID.randomUUID();
        final Session newSession = Session.init(uuid.toString());
        sessionManager.add(newSession);

        return newSession;
    }
}
