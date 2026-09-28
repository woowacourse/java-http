package org.apache.coyote.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 세션을_저장하고_조회한다() {
        // given
        final var session = new Session("session-manager-add");

        // when
        SessionManager.add(session);

        // then
        assertThat(SessionManager.findSession("session-manager-add")).isSameAs(session);

        // cleanup
        SessionManager.remove("session-manager-add");
    }

    @Test
    void 존재하지_않는_세션을_조회하면_null을_반환한다() {
        // when & then
        assertThat(SessionManager.findSession("unknown-session")).isNull();
    }

    @Test
    void 세션을_삭제한다() {
        // given
        final var session = new Session("session-manager-remove");
        SessionManager.add(session);

        // when
        SessionManager.remove("session-manager-remove");

        // then
        assertThat(SessionManager.findSession("session-manager-remove")).isNull();
    }
}
