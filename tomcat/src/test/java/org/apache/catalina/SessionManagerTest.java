package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    @DisplayName("세션 추가 성공")
    void add() {
        // given
        String sessionId = "1";

        assertThat(sessionManager.findSession(sessionId)).isNull();
        Session session = new Session(sessionId);
        sessionManager.add(session);
        assertThat(sessionManager.findSession(sessionId)).isEqualTo(session);

        // after
        sessionManager.remove(session);
    }

    @Test
    @DisplayName("세션 조회 성공: 존재하지 않는 세션인 경우 null을 반환")
    void findSession() {
        String existSessionId = "1";
        String notExistSessionId = "2";

        Session existSession = new Session(existSessionId);
        sessionManager.add(existSession);

        assertAll(
                () -> assertThat(sessionManager.findSession(existSessionId)).isEqualTo(existSession),
                () -> assertThat(sessionManager.findSession(notExistSessionId)).isNull()
        );

        // after
        sessionManager.remove(existSession);
    }

    @Test
    @DisplayName("세션 삭제 성공")
    void remove() {
        // given
        String existSessionId = "1";
        sessionManager.add(new Session(existSessionId));
        Session savedSession = sessionManager.findSession(existSessionId);

        // when
        sessionManager.remove(savedSession);

        // then
        assertThat(sessionManager.findSession(savedSession.getId())).isNull();
    }
}
