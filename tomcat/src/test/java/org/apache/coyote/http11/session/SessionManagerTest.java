package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    SessionManager sessionManager;

    @BeforeEach
    void init() {
        sessionManager = new SessionManager();
    }

    @Test
    void session_manager_에_추가할_수_있다() {
        // given
        final Session session = new Session();

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession(session.getId())).isEqualTo(session);
        ;
    }

    @Test
    void session을_등록하고_등록한_session을_찾을수_있다() {
        // given
        final Session session = new Session();
        sessionManager.add(session);

        // when
        final var findSession = sessionManager.findSession(session.getId());

        // then
        assertThat(findSession).isEqualTo(session);
    }

    @Test
    void 등록된_session_을_삭제할_수_있다() {
        // given
        final Session session = new Session();
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession(session.getId())).isNull();
    }
}
