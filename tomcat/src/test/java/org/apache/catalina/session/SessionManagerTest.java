package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.INSTANCE;

    @Test
    void 세션을_생성하면_id로_찾을_수_있다() {
        final Session session = sessionManager.createSession();

        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void 세션마다_다른_id를_발급한다() {
        final Session first = sessionManager.createSession();
        final Session second = sessionManager.createSession();

        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    void 등록되지_않은_id로_찾으면_null을_반환한다() {
        assertThat(sessionManager.findSession("등록된적_없는_id")).isNull();
    }

    @Test
    void id가_null이면_null을_반환한다() {
        assertThat(sessionManager.findSession(null)).isNull();
    }

    @Test
    void 제거한_세션은_찾을_수_없다() {
        final Session session = sessionManager.createSession();

        sessionManager.remove(session.getId());

        assertThat(sessionManager.findSession(session.getId())).isNull();
    }
}
