package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    void parsesCookieHeader() {
        final HttpCookie cookies = HttpCookie.parse(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=session-id");

        assertThat(cookies.get("yummy_cookie")).contains("choco");
        assertThat(cookies.get("tasty_cookie")).contains("strawberry");
        assertThat(cookies.get(HttpCookie.JSESSIONID)).contains("session-id");
    }

    @Test
    void returnsEmptyCookiesWhenHeaderIsNull() {
        final HttpCookie cookies = HttpCookie.parse(null);

        assertThat(cookies.get(HttpCookie.JSESSIONID)).isEmpty();
    }

    @Test
    void returnsEmptyCookiesWhenHeaderIsEmpty() {
        final HttpCookie cookies = HttpCookie.parse("");

        assertThat(cookies.get(HttpCookie.JSESSIONID)).isEmpty();
    }

    @Test
    void ignoresCookieWithoutEqualsSign() {
        final HttpCookie cookies = HttpCookie.parse("invalid-cookie; JSESSIONID=session-id");

        assertThat(cookies.get("invalid-cookie")).isEmpty();
        assertThat(cookies.get(HttpCookie.JSESSIONID)).contains("session-id");
    }
}
