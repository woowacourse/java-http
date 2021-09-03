package nextstep.jwp.httpserver.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("쿠키 도메인 단위 테스트")
class CookieTest {

    @Test
    @DisplayName("쿠키가 JSESSIONID인 경우")
    void isSessionId() {
        // given
        Cookie cookie = new Cookie("JSESSIONID", "ABCDE");

        // when
        boolean isSessionId = cookie.isSessionId();

        // then
        assertThat(isSessionId).isTrue();
    }

    @Test
    @DisplayName("쿠키가 JSESSIONID가 아닌 경우")
    void noSessionId() {
        // given
        Cookie cookie = new Cookie("name", "air");

        // when
        boolean isSessionId = cookie.isSessionId();

        // then
        assertThat(isSessionId).isFalse();
    }
}
