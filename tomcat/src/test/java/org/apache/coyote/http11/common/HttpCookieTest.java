package org.apache.coyote.http11.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    @DisplayName("쿠키 값을 찾을 수 있다.")
    void get() {
        HttpCookie httpCookie = new HttpCookie("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        String result = httpCookie.get("yummy_cookie");

        assertThat(result).isEqualTo("choco");
    }
}
