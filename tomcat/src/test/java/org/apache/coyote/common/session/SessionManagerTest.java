package org.apache.coyote.common.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SessionManager 는 ")
class SessionManagerTest {

    @DisplayName("세션을 추가한다.")
    @Test
    void addSession() {
        final SessionManager sessionManager = new SessionManager();
        final Session session = new Session("key");
        sessionManager.add("key", session);

        assertThat(sessionManager.findSession("key")).isEqualTo(session);
    }

    @DisplayName("세션을 삭제한다.")
    @Test
    void removeSession() {
        final SessionManager sessionManager = new SessionManager();
        final Session session = new Session("key");
        sessionManager.add("key", session);

        sessionManager.remove(session);

        assertThatThrownBy(() -> sessionManager.findSession("key"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Session does not exist.");
    }
}
