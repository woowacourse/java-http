package org.apache.coyote.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    @DisplayName("SessionManager 를 싱글톤으로 관리한다.")
    void singletonTest() {
        SessionManager sessionManager1 = SessionManager.getInstance();
        SessionManager sessionManager2 = SessionManager.getInstance();

        assertThat(sessionManager1).isSameAs(sessionManager2);
    }

    @Test
    @DisplayName("Session 을 저장하고 session id 값으로 다시 조회할 수 있다.")
    void sessionAddAndFindTest() {
        String sessionId = "sessionId";
        Session session = new Session(sessionId);
        SessionManager sessionManager = SessionManager.getInstance();

        sessionManager.add(session);

        assertThat(sessionManager.findSession(sessionId))
                .isEqualTo(session);
    }

    @Test
    @DisplayName("Session 을 삭제하면 session id 값으로 조회했을 때 null 이 나온다.")
    void sessionRemoveAndFindTest() {
        String sessionId = "sessionId";
        Session session = new Session(sessionId);
        SessionManager sessionManager = SessionManager.getInstance();

        sessionManager.add(session);
        sessionManager.remove(sessionId);

        assertThat(sessionManager.findSession(sessionId))
                .isNull();
    }
}
