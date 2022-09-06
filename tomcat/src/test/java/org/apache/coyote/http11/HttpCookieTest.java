package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.exception.InvalidHttpRequestFormatException;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void HTTP_쿠키_헤더를_파싱해서_객체를_생성하는_테스트() {
        // given
        String cookieHeaderString = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie httpCookie = HttpCookie.parseCookie(cookieHeaderString);

        assertAll(
                () -> assertThat(httpCookie.get("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(httpCookie.get("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(httpCookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @Test
    void HTTP_쿠키_형식이_잘못되면_예외를_반환한다() {
        // given
        String cookieHeaderString = "cookie";

        // when, then
        assertThatThrownBy(() -> HttpCookie.parseCookie(cookieHeaderString))
                .isExactlyInstanceOf(InvalidHttpRequestFormatException.class);
    }
}
