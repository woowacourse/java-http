package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    void 여러_쿠키를_파싱한다() {
        final HttpCookie cookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=session-id"
        );

        assertThat(cookie.getValue("yummy_cookie")).contains("choco");
        assertThat(cookie.getValue("tasty_cookie")).contains("strawberry");
        assertThat(cookie.getValue("JSESSIONID")).contains("session-id");
    }

    @Test
    void JSESSIONID가_없으면_빈_값을_반환한다() {
        final HttpCookie cookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry"
        );

        assertThat(cookie.getValue("JSESSIONID")).isEmpty();
    }

    @Test
    void Cookie_헤더가_없으면_빈_값을_반환한다() {
        final HttpCookie cookie = HttpCookie.from(null);

        assertThat(cookie.getValue("JSESSIONID")).isEmpty();
    }
}