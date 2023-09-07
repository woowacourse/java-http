package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private static final String SESSION_UNIQUE_ID = "sessionUniqueId";

    @DisplayName("새로운 세션을 추가할 수 있다.")
    @Test
    void add() {
        // given
        final SessionManager sessionManager = new SessionManager();
        final Session session = new Session(SESSION_UNIQUE_ID);

        // when & then
        assertAll(
                () -> assertThat(sessionManager.findSession(SESSION_UNIQUE_ID)).isNull(),
                () -> sessionManager.add(session),
                () -> assertThat(sessionManager.findSession(SESSION_UNIQUE_ID)).isEqualTo(session)
        );
    }

    @DisplayName("세션을 삭제할 수 있다.")
    @Test
    void remove() {
        // given
        final SessionManager sessionManager = new SessionManager();
        final Session session = new Session(SESSION_UNIQUE_ID);
        sessionManager.add(session);

        // when & then
        assertAll(
                () -> assertThat(sessionManager.findSession(SESSION_UNIQUE_ID)).isEqualTo(session),
                () -> sessionManager.remove(session),
                () -> assertThat(sessionManager.findSession(SESSION_UNIQUE_ID)).isNull()
        );
    }
}
