package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    @DisplayName("쿠키를 생성한다")
    void createCookie() {
        String cookieHeader = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        Cookie cookie = new Cookie(cookieHeader);

        assertThat(cookie.getCookies().get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        assertThat(cookie.getCookies()).hasSize(1);
    }

    @Test
    @DisplayName("쿠키를 2개 생성한다")
    void createTwoCookies() {
        String cookieHeader = "yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        Cookie cookie = new Cookie(cookieHeader);

        assertThat(cookie.getCookies().get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        assertThat(cookie.getCookies()).hasSize(2);
    }

    @Test
    @DisplayName("잘못된 형태의 쿠키는 저장되지 않는다.")
    void createWrongCookie() {
        String cookieHeader = "yummy_cookiechoco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        Cookie cookie = new Cookie(cookieHeader);

        assertThat(cookie.getCookies().get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        assertThat(cookie.getCookies()).hasSize(1);
    }
}
