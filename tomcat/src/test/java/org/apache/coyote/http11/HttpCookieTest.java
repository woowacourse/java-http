package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void parsesCookies() {
        HttpCookie cookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");
        assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry");
        assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @Test
    void handlesMissingCookieHeader() {
        HttpCookie cookie = HttpCookie.from(null);

        assertThat(cookie.contains("JSESSIONID")).isFalse();
        assertThat(cookie.get("JSESSIONID")).isNull();
    }

    @Test
    void parsesCookieValueContainingEquals() {
        HttpCookie cookie = HttpCookie.from("token=abc=def");

        assertThat(cookie.get("token")).isEqualTo("abc=def");
    }

    @Test
    void parsesEmptyCookieValue() {
        HttpCookie cookie = HttpCookie.from("token=");

        assertThat(cookie.contains("token")).isTrue();
        assertThat(cookie.get("token")).isEmpty();
    }
}
