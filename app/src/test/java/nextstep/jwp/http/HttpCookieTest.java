package nextstep.jwp.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {
    @Test
    @DisplayName("Cookie를 잘 parsing하는지 확인한다")
    void parseCookie() {
        String rawCookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        HttpCookie httpCookie = new HttpCookie(rawCookie);
        assertThat(httpCookie.get("yummy_cookie")).isEqualTo("choco");
        assertThat(httpCookie.get("tasty_cookie")).isEqualTo("strawberry");
        assertThat(httpCookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
