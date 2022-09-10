package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @BeforeEach
    void setUp() {
        SessionManager.getInstance().clear();
    }

    @Test
    @DisplayName("세션에 유저정보를 저장하고 찾는다.")
    void saveAndFind() {
        final Session session = new Session();
        sessionManager.add(session);

        final Session findSession = sessionManager.findSession(session.getId())
                .get();

        assertThat(findSession.getId()).isEqualTo(session.getId());
    }

    @Test
    @DisplayName("세션 타임아웃이 지나면 세션이 삭제된다.")
    void setSessionTimeout() throws InterruptedException {
        final Session session = new Session(1);

        Thread.sleep(2);

        final Optional<Session> findSession = sessionManager.findSession(session.getId());

        assertThat(findSession).isEmpty();
    }
}
