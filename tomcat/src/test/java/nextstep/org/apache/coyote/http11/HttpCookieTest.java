package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("헤더 쿠키를 활용해서 쿠키에 들어있는 데이터를 파싱한다.")
    void parseCookie() {
        // given
        final String cookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        final HttpCookie httpCookie = new HttpCookie(cookie);

        // then
        assertAll(
                () -> assertThat(httpCookie.getCookieValue("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(httpCookie.getCookieValue("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(httpCookie.getCookieValue("JSESSIONID")).isEqualTo(
                        "656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }
}
