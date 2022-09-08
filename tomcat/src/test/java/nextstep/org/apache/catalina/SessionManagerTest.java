package nextstep.org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.apache.catalina.startup.session.Session;
import org.apache.catalina.startup.session.SessionManager;
import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    private final SessionManager sessionManager = new SessionManager();

    @Test
    void findSession() {
        final User user = new User("brorae", "password", "brorae@email.com");
        final Session session = new Session("1234");
        session.setAttribute("user", user);
        sessionManager.add(session);

        final Session foundSession = sessionManager.findSession("1234");
        assertThat((User) foundSession.getAttribute("user")).isEqualTo(user);
    }
}
