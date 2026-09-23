package org.apache.catalina;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void JSESSIONID가_없으면_세션_조회_결과가_비어있다() {
        // given
        String sessionId = null;

        // when
        Optional<Session> session = SessionManager.find(sessionId);

        // then
        assertThat(session).isEmpty();
    }

    @Test
    void 생성한_세션의_ID로_조회하면_동일한_세션을_반환한다() {
        // given
        Session createdSession = SessionManager.create();

        // when
        Session foundSession = SessionManager.find(createdSession.getId()).orElseThrow();

        // then
        assertThat(foundSession).isSameAs(createdSession);
    }

    @Test
    void 등록되지_않은_JSESSIONID로_조회하면_세션_조회_결과가_비어있다() {
        // given
        String sessionId = "unknown-session-id";

        // when
        Optional<Session> session = SessionManager.find(sessionId);

        // then
        assertThat(session).isEmpty();
    }

    @Test
    void 세션을_생성하면_UUID를_세션_ID로_부여한다() {
        // when
        Session session = SessionManager.create();

        // then
        assertThatValueIsUuid(session.getId());
    }

    @Test
    void 세션_attribute를_저장하고_조회한다() {
        // given
        Session session = SessionManager.create();

        // when
        session.setAttribute("user", "usher");

        // then
        assertThat(session.getAttribute("user")).isEqualTo("usher");
    }

    private void assertThatValueIsUuid(String value) {
        assertThat(UUID.fromString(value)).isNotNull();
    }
}
