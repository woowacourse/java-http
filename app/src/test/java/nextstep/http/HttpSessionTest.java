package nextstep.http;

import nextstep.jwp.http.HttpSession;
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
    void hasAttribute() {
        httpSession.setAttribute("user", "user");

        assertThat(httpSession.hasAttribute("user")).isTrue();
        assertThat(httpSession.hasAttribute("password")).isFalse();
    }

    @Test
    void removeAttribute() {
        httpSession.setAttribute("user", "user");

        httpSession.removeAttribute("user");
        assertThat(httpSession.hasAttribute("user")).isFalse();
    }
}
