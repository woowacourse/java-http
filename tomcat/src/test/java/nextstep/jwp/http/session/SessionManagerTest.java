package nextstep.jwp.http.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 없는_Session을_입력받는_경우_empty를_반환한다() {
        SessionManager.add(new Session("testId"));
        Optional<Session> actual = SessionManager.findSession("idd");

        assertThat(actual).isEmpty();
    }

    @Test
    void session을_반환한다() {
        SessionManager.add(new Session("testId"));
        Optional<Session> actual = SessionManager.findSession("testId");

        assertThat(actual).isNotEmpty();
    }
}
