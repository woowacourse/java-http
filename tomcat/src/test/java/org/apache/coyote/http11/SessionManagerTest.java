package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 새로운_세션을_생성하고_저장한다() {
        final SessionManager sessionManager = SessionManager.getInstance();

        final Session session = sessionManager.createNewSession();

        assertThat(sessionManager.findSession(session.id())).isSameAs(session);

        sessionManager.remove(session.id());
    }
}
