package org.apache.coyote.http11.message.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("Cookie를 헤더의 값으로 생성한다.")
    @Test
    void getHeaderValue() {
        // given
        HttpCookie httpCookie = HttpCookie.of("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");

        // when
        String actual = httpCookie.getHeaderValue();

        // then
        assertThat(actual).isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("여러개의 값이 들어있는 Cookie를 헤더의 값으로 생성한다.")
    @Test
    void getHeaderValue_withMultipleValue() {
        // given
        String header = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpCookie httpCookie = HttpCookie.parse(header);

        // when
        String actual = httpCookie.getHeaderValue();

        // then
        assertThat(actual).isEqualTo(header);
    }

    @DisplayName("Cookie 헤더에 들어있는 텍스트로 객체를 생성한다.")
    @Test
    void parse() {
        // given
        String header = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie httpCookie = HttpCookie.parse(header);

        // then
        assertAll(() -> {
            assertThat(httpCookie.getCookie("yummy_cookie").get()).isEqualTo("choco");
            assertThat(httpCookie.getCookie("tasty_cookie").get()).isEqualTo("strawberry");
            assertThat(httpCookie.getCookie("JSESSIONID").get()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        });
    }

    @DisplayName("JSESSIONID에 대한 쿠키를 생성한다.")
    @Test
    void sessionId() {
        // given
        String sessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie httpCookie = HttpCookie.sessionId(sessionId);
        String actual = httpCookie.getCookie("JSESSIONID").get();

        // then
        assertThat(actual).isEqualTo(sessionId);

    }
}
