package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 등록한_세션을_ID로_찾는다() {
        Manager manager = new SessionManager();
        Session session = new Session("session-id");

        manager.add(session);

        assertThat(manager.findSession("session-id"))
                .contains(session);
    }

    @Test
    void 존재하지_않는_세션은_빈_결과를_반환한다() {
        Manager manager = new SessionManager();

        assertThat(manager.findSession("unknown"))
                .isEmpty();
    }

    @Test
    void 세션을_제거하면_더_이상_찾을_수_없다() {
        Manager manager = new SessionManager();
        Session session = new Session("session-id");
        manager.add(session);

        manager.remove("session-id");

        assertThat(manager.findSession("session-id"))
                .isEmpty();
    }

    @Test
    void 세션을_생성하고_저장한다() {
        Manager manager = new SessionManager();

        Session session = manager.createSession();

        assertThat(manager.findSession(session.getId()))
                .contains(session);
    }

    @Test
    void 세션을_갱신하면_기존_세션을_제거하고_새로운_세션을_저장한다() {
        Manager manager = new SessionManager();
        Session oldSession = manager.createSession();

        Session newSession = manager.renewSession(oldSession);

        assertThat(manager.findSession(oldSession.getId()))
                .isEmpty();
        assertThat(manager.findSession(newSession.getId()))
                .contains(newSession);
        assertThat(newSession.getId())
                .isNotEqualTo(oldSession.getId());
    }
}
