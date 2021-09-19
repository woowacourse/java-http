package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HttpSessionsTest {

    @Test
    void getSession() {
        final HttpSession httpSession = new HttpSession("test");
        final HttpSession actual = HttpSessions.getSession("test");

        assertThat(actual.getId()).isEqualTo(httpSession.getId());
    }

    @Test
    void remove() {
        final HttpSession httpSession = new HttpSession("test");
        HttpSessions.remove("test");

        final HttpSession actual = HttpSessions.getSession("test");

        assertThat(actual).isNotEqualTo(httpSession);
    }
}