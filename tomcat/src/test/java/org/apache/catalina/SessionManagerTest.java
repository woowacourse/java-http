package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    @Test
    void session_add_success() {
        // given
        SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("session-id");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("session-id"))
                .isEqualTo(session);
    }

    @Test
    void session_find_success() {
        // given
        SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("session-id");
        sessionManager.add(session);

        // when
        final Session foundSession =
                sessionManager.findSession("session-id");

        // then
        assertThat(foundSession)
                .isEqualTo(session);
    }

    @Test
    void session_remove_success() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("session-id");
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession("session-id"))
                .isNull();
    }

}
