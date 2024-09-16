package org.apache.coyote.http11.message.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.message.common.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieParserTest {

    @DisplayName("여러개의 쿠키값을 파싱할 수 있다.")
    @Test
    void parseSuccess() {
        String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpCookie httpCookie = HttpCookieParser.parse(cookies);

        assertAll(
                () -> assertThat(httpCookie.get("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(httpCookie.get("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(httpCookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46"),
                () -> assertThat(httpCookie.hasSessionId()).isTrue()
        );
    }

    @DisplayName("JSESSIONID를 파싱할 수 있다.")
    @Test
    void parseJSessionIdSuccess() {
        String cookies = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpCookie httpCookie = HttpCookieParser.parse(cookies);

        assertAll(
                () -> assertThat(httpCookie.hasSessionId()).isTrue(),
                () -> assertThat(httpCookie.getSessionId()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @DisplayName("key=value 형식의 쿠키값이 아니면 예외가 발생한다.")
    @Test
    void parseFailure() {
        String cookies = "JSESSIONID:656cef62-e3c4-40bc-a8df-94732920ed46";

        assertThatThrownBy(() -> HttpCookieParser.parse(cookies))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
