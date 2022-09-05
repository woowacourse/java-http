package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("request로 온 쿠키 목록을 저장한다.")
    @Test
    void checkCookies() {
        final HttpCookie httpCookie = new HttpCookie(
                "yummy_cookie=choco; "
                + "tasty_cookie=strawberry; "
                + "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(httpCookie.containsKey("yummy_cookie")).isTrue();
        assertThat(httpCookie.containsKey("tasty_cookie")).isTrue();
        assertThat(httpCookie.containsKey("JSESSIONID")).isTrue();
    }
}
