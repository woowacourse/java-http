package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    @DisplayName("요청한 세션의 id가 없는 경우 예외가 발생한다.")
    void no_id_in_session_manager_throw() {
        SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = UUID.randomUUID().toString();

        assertThatThrownBy(() -> sessionManager.findSession(sessionId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
