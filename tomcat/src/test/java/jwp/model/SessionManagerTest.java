package jwp.model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.model.Session;
import nextstep.jwp.model.SessionManager;

public class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getSessionManager();

    @Test
    @DisplayName("세션이 존재하는지 확인한다.")
    void containsSession() {
        // given
        Session session = new Session("123456");
        sessionManager.add(session);

        Session notRegisteredSession = new Session("234567");

        // when, then
        assertAll(
            () -> assertThat(sessionManager.containsSession(session)).isTrue(),
            () -> assertThat(sessionManager.containsSession(notRegisteredSession)).isFalse()
        );
    }

    @Test
    @DisplayName("id로 세션을 찾아 반환한다.")
    void findSessionById() {
        // given
        Session session = new Session("123456");
        sessionManager.add(session);

        // when
        Optional<Session> foundSession = sessionManager.findSession(session.getId());

        // when, then
        assertThat(foundSession.get()).isEqualTo(session);
    }

    @Test
    @DisplayName("해당하는 세션이 존재하지 않으면 빈 값을 반환한다.")
    void notExistSession() {
        // given
        Session session = new Session("123456");
        sessionManager.add(session);

        // when
        Optional<Session> foundSession = sessionManager.findSession("wrongId");

        // when, then
        assertThat(foundSession).isEmpty();
    }
}
