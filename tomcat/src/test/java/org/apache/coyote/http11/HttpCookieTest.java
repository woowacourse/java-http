package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    void parsesMultipleCookies() {
        final var cookies = new HttpCookie("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=session-id");

        assertThat(cookies.getValue("yummy_cookie")).isEqualTo("choco");
        assertThat(cookies.getValue("tasty_cookie")).isEqualTo("strawberry");
        assertThat(cookies.getValue("JSESSIONID")).isEqualTo("session-id");
    }

    @Test
    void preservesEqualsSignAndIgnoresInvalidCookie() {
        final var cookies = new HttpCookie("invalid; token=abc==; JSESSIONID = session-id");

        assertThat(cookies.getValue("token")).isEqualTo("abc==");
        assertThat(cookies.getValue("invalid")).isNull();
        assertThat(cookies.getValue("JSESSIONID")).isEqualTo("session-id");
    }
}
