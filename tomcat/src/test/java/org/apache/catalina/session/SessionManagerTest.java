package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = SessionManager.getInstance();
    }

    @Test
    void addSessionTest() {
        Session session = new Session("test-session-id");

        sessionManager.add(session);

        assertThat(sessionManager.findSession("test-session-id")).isEqualTo(session);
    }

    @Test
    void findSessionTest_whenSessionExists() {
        Session session = new Session("existing-session-id");
        sessionManager.add(session);

        Session foundSession = sessionManager.findSession("existing-session-id");

        assertThat(foundSession).isEqualTo(session);
    }

    @Test
    void findSessionTest_whenSessionDoesNotExist_shouldReturnNull() {
        Session session = sessionManager.findSession("nonexistent-session-id");

        assertThat(session).isNull();
    }

    @Test
    void removeSessionTest() {
        Session session = new Session("test-session-id");
        sessionManager.add(session);

        sessionManager.remove(session);

        assertThat(sessionManager.findSession("test-session-id")).isNull();
    }
}
