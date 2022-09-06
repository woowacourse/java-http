package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {


    @Test
    void 세션을_저장한다() {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("id");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("id")).isEqualTo(session);
    }

    @Test
    void 세션을_제거한다() {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("id");
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession("id")).isNull();
    }
}
