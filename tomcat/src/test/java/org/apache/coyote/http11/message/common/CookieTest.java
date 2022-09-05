package org.apache.coyote.http11.message.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @DisplayName("Cookie를 헤더의 값으로 생성한다.")
    @Test
    void getHeaderValue() {
        // given
        Cookie cookie = Cookie.of("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");

        // when
        String actual = cookie.getHeaderValue();

        // then
        assertThat(actual).isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("여러개의 값이 들어있는 Cookie를 헤더의 값으로 생성한다.")
    @Test
    void getHeaderValue_withMultipleValue() {
        // given
        String header = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        Cookie cookie = Cookie.parse(header);

        // when
        String actual = cookie.getHeaderValue();

        // then
        assertThat(actual).isEqualTo(header);
    }

    @DisplayName("Cookie 헤더에 들어있는 텍스트로 객체를 생성한다.")
    @Test
    void parse() {
        // given
        String header = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        Cookie cookie = Cookie.parse(header);

        // then
        assertAll(() -> {
            assertThat(cookie.getCookie("yummy_cookie").get()).isEqualTo("choco");
            assertThat(cookie.getCookie("tasty_cookie").get()).isEqualTo("strawberry");
            assertThat(cookie.getCookie("JSESSIONID").get()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        });
    }

    @DisplayName("JSESSIONID에 대한 쿠키를 생성한다.")
    @Test
    void sessionId() {
        // given
        String sessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        Cookie cookie = Cookie.sessionId(sessionId);
        String actual = cookie.getCookie("JSESSIONID").get();

        // then
        assertThat(actual).isEqualTo(sessionId);

    }
}
