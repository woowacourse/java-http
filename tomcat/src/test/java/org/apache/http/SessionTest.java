package org.apache.http;

import nextstep.jwp.http.Session;
import nextstep.jwp.http.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    final SessionManager sessionManager = SessionManager.get();
    String sessionId = null;

    @AfterEach
    void setDown() {
        if (sessionId != null) {
            final Session session = sessionManager.findSession(sessionId);
            sessionManager.remove(session);
        }
    }

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

        final String sessionId = "sessionId";
        final Session session = new Session(sessionId);
        sessionManager.add(session);

        // when
        session.invalidate();

        // then
        assertThat(sessionManager.findSession(sessionId)).isNull();
    }
}
