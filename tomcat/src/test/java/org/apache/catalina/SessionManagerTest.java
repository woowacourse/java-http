package org.apache.catalina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("세션 관리자")
class SessionManagerTest {

    private final SessionManager manager = SessionManager.getInstance();

    @Test
    @DisplayName("생성한 세션을 식별자로 조회한다")
    void findsCreatedSessionById() {
        // given
        final var session = manager.createSession();

        // when
        final var found = manager.findSession(session.getId());

        // then
        assertThat(found).isSameAs(session);
    }

    @Test
    @DisplayName("서로 다른 세션에 서로 다른 식별자를 발급한다")
    void createsUniqueSessionIds() {
        // given
        final var first = manager.createSession();

        // when
        final var second = manager.createSession();

        // then
        assertThat(second.getId()).isNotEqualTo(first.getId());
    }
}
