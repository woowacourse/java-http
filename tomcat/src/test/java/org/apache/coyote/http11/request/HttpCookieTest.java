package org.apache.coyote.http11.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    void 여러_쿠키를_이름으로_구분해_파싱한다() {
        final HttpCookie cookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62");

        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");
        assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry");
        assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62");
    }

    @Test
    void 값에_포함된_등호는_값의_일부로_남는다() {
        final HttpCookie cookie = HttpCookie.from("token=YWJjZA==; JSESSIONID=abc");

        assertThat(cookie.get("token")).isEqualTo("YWJjZA==");
    }

    @Test
    void 쿠키_헤더가_없으면_빈_쿠키를_반환한다() {
        final HttpCookie cookie = HttpCookie.from(null);

        assertThat(cookie.get("JSESSIONID")).isNull();
    }

    @Test
    void 없는_이름을_조회하면_null을_반환한다() {
        final HttpCookie cookie = HttpCookie.from("JSESSIONID=abc");

        assertThat(cookie.get("yummy_cookie")).isNull();
    }
}
