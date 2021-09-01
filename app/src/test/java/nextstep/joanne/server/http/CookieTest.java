package nextstep.joanne.server.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CookieTest {

    @DisplayName("쿠키를 생성한다.")
    @Test
    void create() {
        // given
        String sCookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        Cookie cookie = new Cookie(sCookie);

        // then
        assertThat(cookie.getCookies()).containsKeys("yummy_cookie", "tasty_cookie", "JSESSIONID");
        assertThat(cookie.getCookies()).containsValues("choco", "strawberry", "656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("JSESSIONID를 가져온다.")
    @Test
    void getSessionId() {
        // given
        String sCookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        Cookie cookie = new Cookie(sCookie);

        // then
        assertThat(cookie.getSessionId())
                .isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("JSESSIONID가 없는 경우, 랜덤으로 생성한다.")
    @Test
    void makeSessionId() {
        // given
        String sCookie = "yummy_cookie=choco; tasty_cookie=strawberry;";

        // when
        Cookie cookie = new Cookie(sCookie);

        // then
        assertThat(cookie.makeSessionId()).isNotNull();
    }
}