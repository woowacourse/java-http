package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void cookieMapping() {
        HttpCookie cookie = HttpCookie.from("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertAll(
                ()->assertThat(cookie.find("yummy_cookie")).isEqualTo("choco"),
                ()->assertThat(cookie.find("tasty_cookie")).isEqualTo("strawberry"),
                ()->assertThat(cookie.find("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }
}
