package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("세션 저장 시 랜덤한 sessionId를 반환하고 해당 id에 대한 session 을 조회할 수 있다.")
    @Test
    void findSessionById() {
        // given
        Session expected = new Session();
        String sessionId = SessionManager.getInstance().storeSession(expected);

        // when
        Session actual = SessionManager.getInstance().findSessionById(sessionId);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}