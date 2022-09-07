package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void cookieMapping() {
        String cookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        HttpCookie values = HttpCookie.from(cookie);

        assertThat(values.getValues()).containsValues("choco", "strawberry", "656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}