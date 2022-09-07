package org.apache.catalina;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void 세션을_추가할_수_있다() {
        // given
        final String sessionId = "sessionId";
        final Session session = new Session(sessionId);
        final SessionManager sessionManager = SessionManager.get();

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession(sessionId)).isEqualTo(session);
    }

    @Test
    void 세션을_삭제할_수_있다() {
        // given
        final String sessionId = "sessionId";
        final Session session = new Session(sessionId);
        final SessionManager sessionManager = SessionManager.get();
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession(sessionId)).isNull();
    }
}
