package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @AfterEach
    void tearDown() {
        sessionManager.remove("session-id");
        sessionManager.remove("session-to-remove");
    }

    @Test
    void addsAndFindsSession() {
        Session session = new Session("session-id");

        sessionManager.add(session);

        assertThat(sessionManager.findSession("session-id")).isSameAs(session);

        sessionManager.remove("session-id");
    }

    @Test
    void removesSession() {
        Session session = new Session("session-to-remove");
        sessionManager.add(session);

        sessionManager.remove("session-to-remove");

        assertThat(sessionManager.findSession("session-to-remove")).isNull();
    }
}
