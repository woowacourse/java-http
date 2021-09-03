package nextstep.jwp.http;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionsTest {
    public static final String ANY_SESSION_ID = "any-session-id";

    @BeforeEach
    void setUp() {
        HttpSessions.addSession(new HttpSession(ANY_SESSION_ID));
    }

    @Test
    void addSession() {
        assertThat(HttpSessions.contains(ANY_SESSION_ID)).isTrue();
    }

    @Test
    void getSession() {
        HttpSession httpSession = new HttpSession(ANY_SESSION_ID);
        HttpSessions.addSession(httpSession);
        User user = new User(1L, "account", "password", "email");
        assertThat(HttpSessions.getSession(ANY_SESSION_ID, user)).isEqualTo(httpSession);
    }

    @Test
    void getSessionExceptId() {
        HttpSession httpSession = new HttpSession(ANY_SESSION_ID);
        HttpSessions.addSession(httpSession);
        User user = new User(1L, "account", "password", "email");

        String newSessionId = "new-session-id";
        HttpSession newSession = HttpSessions.getSession(newSessionId, user);
        assertThat(newSession.getId()).isEqualTo(newSessionId);
    }
}
