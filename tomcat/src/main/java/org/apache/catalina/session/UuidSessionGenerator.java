package org.apache.catalina.session;

import java.util.UUID;

public class UuidSessionGenerator implements SessionGenerator {

    @Override
    public Session create() {
        String sessionId = UUID.randomUUID().toString();
        return new Session(sessionId);
    }
}
