package nextstep.org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("세션을 생성한다")
    @Test
    void createSession() {
        final Session session = SessionManager.create(new User("slow", "password", "email"));

        assertThat(session).isNotNull();
    }

    @DisplayName("세션을 조회한다")
    @Test
    void findSession() {
        final Session expected = SessionManager.create(new User("slow", "password", "email"));

        final Session actual = SessionManager.findSession(expected.getId());

        assertThat(actual).isEqualTo(expected);
    }
}
