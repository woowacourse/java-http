package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();


    @DisplayName("인자에 null이 들어가면 빈 optional이 반환된다.")
    @Test
    void findSession_nullArgs() {
        Optional<Session> session = sessionManager.findSession(null);
        assertThat(session.isEmpty()).isTrue();
    }
}
