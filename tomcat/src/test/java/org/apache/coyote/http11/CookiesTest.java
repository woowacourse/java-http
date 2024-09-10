package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookiesTest {

    @DisplayName("쿠키 헤더로 쿠키 객체를 생성한다.")
    @Test
    void cookieCreateTest() {
        String cookieHeader = "tasty_cookie=choco; JSESSIONID=jazz";
        Cookies cookie = new Cookies(cookieHeader);

        Map<String, String> cookies = Map.of(
                "tasty_cookie", "choco",
                "JSESSIONID", "jazz"
        );

        assertThat(cookies).isEqualTo(cookie.getCookies());
    }

    @DisplayName("쿠키 객체로 쿠키 헤더를 생성한다.")
    @Test
    void toCookieHeaderTest() {
        String cookieHeader = "tasty_cookie=choco; JSESSIONID=jazz";
        Cookies cookies = new Cookies(cookieHeader);

        assertThat(cookies.toCookieHeader())
                .contains("tasty_cookie=choco")
                .contains("JSESSIONID=jazz");
    }
}
