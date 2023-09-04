package org.apache.coyote.http11.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SessionRepositoryTest {

    private final SessionRepository sessionRepository = new SessionRepository();

    @Test
    void 세션을_생성한다() {
        // given
        final String sessionId = "JSESSIONID";

        // when, then
        assertDoesNotThrow(
                () -> sessionRepository.create(Session.from(sessionId))
        );
    }

    @Test
    void 세션을_조회한다() {
        // given
        final String sessionId = "JSESSIONID";
        sessionRepository.create(Session.from(sessionId));

        // when
        Session session = sessionRepository.getSession(sessionId);

        // then
        Assertions.assertThat(session.getId()).isEqualTo(sessionId);
    }

}
