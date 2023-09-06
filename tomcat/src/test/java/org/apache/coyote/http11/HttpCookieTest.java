package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    private static final String COOKIE_HEADER = "cookie1=one; cookie2=two; cookie3=three";

    @DisplayName("CookieHeader값을 파싱해서 올바를 Cookie 객체를 생성한다.")
    @Test
    void from() {
        // given
        final HttpCookie cookie = HttpCookie.from(COOKIE_HEADER);

        // when & then
        assertAll(
                () -> assertThat(cookie.contains("cookie1")).isTrue(),
                () -> assertThat(cookie.contains("cookie2")).isTrue(),
                () -> assertThat(cookie.contains("cookie3")).isTrue()
        );
    }

    @DisplayName("새로운 쿠키 item 값을 추가할 수 있다.")
    @Test
    void setCookie() {
        // given
        final HttpCookie cookie = HttpCookie.from(COOKIE_HEADER);

        // when
        cookie.setCookie("sungHa", "hi!");

        // then
        assertThat(cookie.contains("sungHa")).isTrue();
    }

    @DisplayName("key에 해당하는 value가 존재하는지 여부를 확인할 수 있다.")
    @Test
    void contains() {
        // given
        final HttpCookie cookie = HttpCookie.from(COOKIE_HEADER);

        // when

        // then
        assertAll(
                () -> assertThat(cookie.contains("cookie1")).isTrue(),
                () -> assertThat(cookie.contains("cookie2")).isTrue(),
                () -> assertThat(cookie.contains("cookie3")).isTrue(),
                () -> assertThat(cookie.contains("cookie4")).isFalse()
        );
    }
}
