package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import org.apache.coyote.http11.session.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HttpCookie 클래스의")
class HttpCookieTest {

    @Nested
    @DisplayName("생성자는")
    class Constructor {

        @Test
        @DisplayName("문자열에 주어진 쿠키 값들을 Map에 저장한다.")
        void success() {
            // given
            final String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

            // when
            final HttpCookie httpCookie = HttpCookie.from(cookies);

            // then
            assertThat(httpCookie.getValues()).contains(
                    entry("yummy_cookie", "choco"),
                    entry("tasty_cookie", "strawberry"),
                    entry("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46")
            );
        }

        @Test
        @DisplayName("쿠키가 올바른 형식이 아닌 경우 예외를 던진다.")
        void invalidCookie_ExceptionThrown() {
            // given
            final String cookies = "yummy_cookie?choco: tasty_cooki?strawberry: JSESSIONID?656cef62-e3c4-40bc-a8df-94732920ed46";

            // when & then
            assertThatThrownBy(() -> HttpCookie.from(cookies))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 application/x-www-form-urlencoded 형식이 아닙니다.");
        }
    }
}
