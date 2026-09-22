package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void parsesCookies() {
        HttpCookie cookie = new HttpCookie(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");
        assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry");
        assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @Test
    void handlesMissingCookieHeader() {
        HttpCookie cookie = new HttpCookie(null);

        assertThat(cookie.contains("JSESSIONID")).isFalse();
        assertThat(cookie.get("JSESSIONID")).isNull();
    }
}
