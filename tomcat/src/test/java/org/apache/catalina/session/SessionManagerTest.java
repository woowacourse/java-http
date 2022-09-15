package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("findSession으로 Session을 반환하고, remove는 Session을 제거한다")
    @Test
    void find_session_and_remove() {
        // given
        final Session session = Session.newInstance();
        final String sessionId = session.getId();

        // when
        final Optional<Session> actualAbsent = SessionManager.findSession("Not Exist ID");
        final Optional<Session> actualExist = SessionManager.findSession(sessionId);
        SessionManager.remove(sessionId);
        final Optional<Session> actualAfterRemove = SessionManager.findSession(sessionId);

        // then
        assertAll(
                () -> assertThat(actualAbsent).isEmpty(),
                () -> assertThat(actualExist).isPresent(),
                () -> assertThat(actualExist.get().getId()).isEqualTo(sessionId),
                () -> assertThat(actualAfterRemove).isEmpty()
        );
    }
}
