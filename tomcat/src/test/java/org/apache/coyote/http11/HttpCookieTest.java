package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    void parsesCookiesFromRequestHeader() {
        HttpCookie cookie = new HttpCookie(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=existing-session-id"
        );

        assertThat(cookie.get("yummy_cookie")).contains("choco");
        assertThat(cookie.get("tasty_cookie")).contains("strawberry");
        assertThat(cookie.get("JSESSIONID")).contains("existing-session-id");
    }
}
