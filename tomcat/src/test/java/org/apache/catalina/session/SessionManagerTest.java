package org.apache.catalina.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @AfterEach
    void tearDown() {
        sessionManager.remove("session-id");
    }

    @Test
    void 세션을_추가하고_ID로_조회한다() {
        final Session session = new Session("session-id");

        sessionManager.add(session);

        assertThat(sessionManager.findSession("session-id")).isSameAs(session);
    }

    @Test
    void 존재하지_않는_세션을_조회하면_null을_반환한다() {
        assertThat(sessionManager.findSession("unknown-session-id")).isNull();
    }

    @Test
    void 세션_ID로_세션을_삭제한다() {
        final Session session = new Session("session-id");
        sessionManager.add(session);

        sessionManager.remove("session-id");

        assertThat(sessionManager.findSession("session-id")).isNull();
    }
}
