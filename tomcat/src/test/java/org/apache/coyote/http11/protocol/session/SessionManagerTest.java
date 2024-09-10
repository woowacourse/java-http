package org.apache.coyote.http11.protocol.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private SessionManager sessionManager;
    private Session session;

    @BeforeEach
    void setUp() {
        sessionManager = SessionManager.getInstance();
        session = new Session("session_id");
        sessionManager.add(session);
    }

    @Test
    @DisplayName("세션을 조회한다.")
    void findSession() {
        Session foundSession = sessionManager.findSession("session_id");

        assertThat(foundSession).isEqualTo(session);
    }

    @Test
    @DisplayName("존재하지 않는 세션을 조회하면 null 을 반환한다.")
    void findNonExistentSession() {
        Session foundSession = sessionManager.findSession("non-existent");

        assertThat(foundSession).isNull();
    }

    @Test
    @DisplayName("세션을 삭제한다.")
    void RemoveSession() {
        sessionManager.remove(session);

        assertThat(sessionManager.findSession("session_id")).isNull();
    }
}
