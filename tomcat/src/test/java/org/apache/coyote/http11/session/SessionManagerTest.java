package org.apache.coyote.http11.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionManagerTest {

    SessionManager sessionManager = new SessionManager(59);

    @Test
    @DisplayName("만료 시간이 지난 세션들을 삭제한다.")
    void clean_session_with_expired() {
        final LocalDateTime time = LocalDateTime.now();

        final Session session1 = sessionManager.createSession(time);
        final Session session2 = sessionManager.createSession(time.plusSeconds(1));
        final Session session3 = sessionManager.createSession(time.plusMinutes(1)
                .plusSeconds(1));

        sessionManager.cleanUpSession(time.plusMinutes(1));


        final Optional<Session> findSession1 = sessionManager.findSession(session1.getId());
        final Optional<Session> findSession3 = sessionManager.findSession(session2.getId());
        final Optional<Session> findSession2 = sessionManager.findSession(session3.getId());

        assertFalse(findSession1.isPresent());
        // 만료시간과 일치하면 삭제한다.
        assertTrue(findSession2.isPresent());
        assertFalse(findSession3.isPresent());
    }
}
