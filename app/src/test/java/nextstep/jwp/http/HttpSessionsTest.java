package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpSessionsTest {

    @DisplayName("id에 맞는 HttpSession이 없으면, 만들어서 반환한다.")
    @Test
    void noIdHttpSessionTest() {
        assertThat(HttpSessions.getSession("123")).isEqualTo(new HttpSession("123"));
    }
}