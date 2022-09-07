package org.apache.catalina;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void Session을_무효화하면_Session내의_데이터가_clear_된다() {
        // given
        final String sessionId = "sessionId";
        final Session session = new Session(sessionId);
        session.setAttribute("key", "value");

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute("key")).isNull();
    }

    @Test
    void Session을_무효화하면_SessionManager에서_삭제된다() {
        // given
        final SessionManager sessionManager = SessionManager.get();
        final String sessionId = "sessionId";
        final Session session = new Session(sessionId);
        sessionManager.add(session);

        // when
        session.invalidate();

        // then
        assertThat(sessionManager.findSession(sessionId)).isNull();
    }
}
