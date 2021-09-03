package nextstep.jwp.http;

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
    void contains() {
        assertThat(HttpSessions.contains(ANY_SESSION_ID)).isTrue();
    }

    @Test
    void addSession() {
        assertThat(HttpSessions.contains(ANY_SESSION_ID)).isTrue();
    }

    @Test
    void getSession() {
        HttpSession httpSession = new HttpSession(ANY_SESSION_ID);
        HttpSessions.addSession(httpSession);
        assertThat(HttpSessions.getSession(ANY_SESSION_ID)).isEqualTo(httpSession);
    }
}
