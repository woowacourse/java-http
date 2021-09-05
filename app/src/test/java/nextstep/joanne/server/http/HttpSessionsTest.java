package nextstep.joanne.server.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpSessionsTest {

    @DisplayName("Session을 가져온다.")
    @Test
    void getSession() {
        // given
        HttpSession httpSession = HttpSessions.getSession("JSESSIONID");
        assertThat(httpSession).isNotNull();
    }

    @DisplayName("Session을 지운다.")
    @Test
    void remove() {
        assertThatCode(() -> HttpSessions.remove("JSESSIONID"))
                .doesNotThrowAnyException();
    }
}