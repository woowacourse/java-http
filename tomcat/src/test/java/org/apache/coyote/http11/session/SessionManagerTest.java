package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void SessionManager는_session의_key값을_이용하여_session을_조회한다() {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("zz");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession(session.getId())).isEqualTo(session);
    }

    @Test
    void SessionManager는_내부의_session을_삭제할_수_있다() {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("zz");

        sessionManager.add(session);
        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession(session.getId())).isEqualTo(null);
    }

    @Test
    void SessionManager에게_존재하지않는_session_삭제_요청을_보낼_수_있다() {
        // given
        SessionManager sessionManager = new SessionManager();

        // when
        Session session = new Session("zz");

        // then
        assertThatCode(() -> sessionManager.remove(session))
                .doesNotThrowAnyException();
    }
}
