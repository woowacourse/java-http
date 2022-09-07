package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @BeforeEach
    void setUp() {
        SessionManager.getInstance().clear();
    }

    @Test
    @DisplayName("세션에 유저정보를 저장하고 찾는다.")
    void saveAndFind() {
        final SessionManager sessionManager = SessionManager.getInstance();
        final long id = 1L;

        final String jSessionId = sessionManager.addSession(id);

        final Long findUserId = sessionManager.findSession(jSessionId)
                .get();

        assertThat(findUserId).isEqualTo(id);
    }

    @Test
    @DisplayName("세션 타임아웃이 지나면 세션이 삭제된다.")
    void setSessionTimeout() throws InterruptedException {
        final SessionManager sessionManager = SessionManager.getInstance();
        final long id = 1L;

        final String jSessionId = sessionManager.addSession(id, 0);

        Thread.sleep(10);

        final Optional<Long> findUserId = sessionManager.findSession(jSessionId);

        assertThat(findUserId).isEmpty();
    }
}
