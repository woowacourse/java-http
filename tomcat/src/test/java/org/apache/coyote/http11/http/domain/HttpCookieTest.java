package org.apache.coyote.http11.http.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void from() {
        HttpCookie httpCookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(httpCookie.getCookies()).containsAllEntriesOf(Map.of(
                "yummy_cookie", "choco",
                "tasty_cookie", "strawberry",
                "JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"
        ));
    }

    @Test
    void containsJSESSIONID() {
        HttpCookie httpCookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(httpCookie.containsJSESSIONID()).isTrue();
    }
}
