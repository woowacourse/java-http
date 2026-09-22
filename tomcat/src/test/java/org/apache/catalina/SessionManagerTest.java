package org.apache.catalina;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void JSESSIONID가_없으면_UUID_세션을_생성한다() {
        // when
        ResolvedSession resolvedSession = SessionManager.resolve(null);

        // then
        assertThat(resolvedSession.isNewSession()).isTrue();
        assertThatValueIsUuid(resolvedSession.session().getId());
    }

    @Test
    void 등록된_JSESSIONID로_같은_세션을_조회한다() {
        // given
        ResolvedSession createdSession = SessionManager.resolve(null);

        // when
        ResolvedSession foundSession = SessionManager.resolve(createdSession.session().getId());

        // then
        assertThat(foundSession.isNewSession()).isFalse();
        assertThat(foundSession.session()).isSameAs(createdSession.session());
    }

    @Test
    void 미등록_JSESSIONID면_새_UUID_세션으로_교체한다() {
        // when
        ResolvedSession resolvedSession = SessionManager.resolve("unknown-session-id");

        // then
        assertThat(resolvedSession.isNewSession()).isTrue();
        assertThat(resolvedSession.session().getId()).isNotEqualTo("unknown-session-id");
        assertThatValueIsUuid(resolvedSession.session().getId());
    }

    @Test
    void 세션_attribute를_저장하고_조회한다() {
        // given
        Session session = SessionManager.resolve(null).session();

        // when
        session.setAttribute("user", "usher");

        // then
        assertThat(session.getAttribute("user")).isEqualTo("usher");
    }

    private void assertThatValueIsUuid(String value) {
        assertThat(UUID.fromString(value)).isNotNull();
    }
}
