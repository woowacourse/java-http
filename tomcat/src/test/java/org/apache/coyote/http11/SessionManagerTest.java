package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void storeSession() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());

        SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);

        assertThat(sessionManager.findSession(uuid.toString())).isNotNull();
    }
}