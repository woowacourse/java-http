package org.apache.coyote.http11.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    @DisplayName("Raw한 쿠키 값으로 쿠키를 생성한다.")
    void from() {
        // given
        final String rawCookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        Cookie cookie = Cookie.from(rawCookie);

        // then
        org.junit.jupiter.api.Assertions.assertAll(
                () -> assertThat(cookie.hasNotKey("yummy_cookie")).isFalse(),
                () -> assertThat(cookie.hasNotKey("tasty_cookie")).isFalse(),
                () -> assertThat(cookie.hasNotKey("JSESSIONID")).isFalse()
        );

    }
}
