package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    void parseCookieHeader() {
        final var cookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        assertThat(cookie.value("yummy_cookie")).contains("choco");
        assertThat(cookie.value("tasty_cookie")).contains("strawberry");
        assertThat(cookie.value("JSESSIONID"))
                .contains("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @Test
    void missingCookieHeaderCreatesEmptyCookie() {
        final var cookie = HttpCookie.from(null);

        assertThat(cookie.value("JSESSIONID")).isEmpty();
    }
}
