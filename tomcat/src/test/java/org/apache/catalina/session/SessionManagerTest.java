package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void addAndFind() {
        // given
        final Session session = new Session("manager-add");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("manager-add")).isSameAs(session);
    }

    @Test
    void remove() {
        // given
        final Session session = new Session("manager-remove");
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession("manager-remove")).isNull();
    }

    @Test
    void findUnknownOrNullId() {
        assertThat(sessionManager.findSession("manager-unknown")).isNull();
        assertThat(sessionManager.findSession(null)).isNull();
    }

    @Test
    void singleton() {
        assertThat(SessionManager.getInstance()).isSameAs(sessionManager);
    }
}
