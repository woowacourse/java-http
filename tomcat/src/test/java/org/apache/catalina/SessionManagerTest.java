package org.apache.catalina;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void createFindAndRemoveSession() {
        final var session = SessionManager.create();

        assertThat(SessionManager.findSession(session.getId())).contains(session);

        SessionManager.remove(session.getId());

        assertThat(SessionManager.findSession(session.getId())).isEmpty();
    }
}
