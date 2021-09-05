package nextstep.jwp.http;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {
    private HttpCookie httpCookie = HttpCookie.of("yummy_cookie=choco; tasty_cookie=strawberry;");

    @DisplayName("쿠키값을 가지고 있는지 확인한다.")
    @Test
    void hasCookie() {
        Assertions.assertThat(httpCookie.hasCookie("yummy_cookie")).isTrue();
        Assertions.assertThat(httpCookie.hasCookie("bad_cookie")).isFalse();
    }

    @DisplayName("쿠키값을 얻어온다.")
    @Test
    void getCookie() {
        Assertions.assertThat(httpCookie.getCookie("yummy_cookie")).isEqualTo("choco");
        Assertions.assertThat(httpCookie.getCookie("tasty_cookie")).isEqualTo("strawberry");
    }
}