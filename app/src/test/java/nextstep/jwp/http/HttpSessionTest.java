package nextstep.jwp.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionTest {
    public static final String COOKIE_ID = "cookie-id";
    private HttpSession httpSession;

    @BeforeEach
    void setUp() {
        httpSession = new HttpSession(COOKIE_ID);
    }

    @Test
    void makeSession() {
        assertThat(httpSession.getId()).isEqualTo(COOKIE_ID);
    }

    @Test
    void setAttribute() {
        httpSession.setAttribute("user", "user");

        assertThat(httpSession.containsAttribute("user")).isTrue();
        assertThat(httpSession.containsAttribute("password")).isFalse();
    }

    @Test
    void removeAttribute() {
        httpSession.setAttribute("user", "user");

        httpSession.removeAttribute("user");
        assertThat(httpSession.containsAttribute("user")).isFalse();
    }
}
