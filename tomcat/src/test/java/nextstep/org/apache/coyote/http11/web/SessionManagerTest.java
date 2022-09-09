package nextstep.org.apache.coyote.http11.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.apache.coyote.http11.web.Session;
import org.apache.coyote.http11.web.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    @DisplayName("SessionManager가 Session을 조회한다.")
    @Test
    void findSession() {
        // given
        final Session session = new Session("id");
        SessionManager.add(session);

        // when
        final Optional<Session> optionalSession = SessionManager.findSession("id");

        // then
        assertTrue(optionalSession.isPresent());
    }
}
