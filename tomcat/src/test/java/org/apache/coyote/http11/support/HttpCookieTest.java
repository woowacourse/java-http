package org.apache.coyote.http11.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

class HttpCookieTest {

    @DisplayName("쿠키를 새로 생성할 경우, JSESSIONID가 포함되서 생성된다.")
    @Test
    void create() {
        // given, when
        final HttpCookie httpCookie = HttpCookie.create();

        // then
        assertThat(httpCookie.getJSessionId()).isNotNull();
    }

    @DisplayName("string으로 받아온 cookie로부터 HttpCookie 객체를 생성한다.")
    @Test
    void from() {
        // given
        final String cookie = "yummy_cookie=choco; " +
                "tasty_cookie=strawberry; " +
                "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        final HttpCookie httpCookie = HttpCookie.from(cookie);

        // then
        assertThat(httpCookie.format()).contains(
                List.of("yummy_cookie=choco",
                        "tasty_cookie=strawberry",
                        "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }
}
