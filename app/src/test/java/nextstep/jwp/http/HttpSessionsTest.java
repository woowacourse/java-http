package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpSessionsTest {

    @DisplayName("동일한 id의 세션을 가져오는지 확인")
    @Test
    void getSession() {
        final HttpSession httpSession = new HttpSession("test");
        final HttpSession actual = HttpSessions.getSession("test");

        assertThat(actual.getId()).isEqualTo(httpSession.getId());
    }

    @DisplayName("세션을 지운 뒤 가져올 때 생성해서 반환")
    @Test
    void remove() {
        final HttpSession httpSession = new HttpSession("test");
        HttpSessions.remove("test");

        final HttpSession actual = HttpSessions.getSession("test");

        assertThat(actual).isNotEqualTo(httpSession);
    }
}