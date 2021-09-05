package nextstep.http;

import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionsTest {
    public static final String SESSION_ID = "any-session-id";

    @BeforeEach
    void setUp() {
        HttpSessions.add(SESSION_ID, new HttpSession(SESSION_ID));
    }

    @Test
    void add() {
        assertThat(HttpSessions.contains(SESSION_ID)).isTrue();
    }

    @Test
    void getSession() {
        HttpSession httpSession = new HttpSession(SESSION_ID);
        HttpSessions.add(SESSION_ID, httpSession);
        assertThat(HttpSessions.find(SESSION_ID)).isEqualTo(httpSession);
    }
}

