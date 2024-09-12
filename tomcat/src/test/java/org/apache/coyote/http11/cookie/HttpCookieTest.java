package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpCookieTest {

    @DisplayName("Cookie를 key-value 형태로 만든다.")
    @Test
    void testConvertToCookie() {
        // given
        final String cookieLine = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=abcdefghijklmnopqrstuvwxyz";

        // when
        final HttpCookie cookie = new HttpCookie(cookieLine);
        final Map<String, String> actual = cookie.getCookie();

        // then
        final Map<String, String> expected = Map.of("yummy_cookie", "choco", "tasty_cookie", "strawberry",
                "JSESSIONID", "abcdefghijklmnopqrstuvwxyz");
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("JSESSIONID를 가져올 수 있다.")
    @Test
    void testGetJSessionId() {
        // given
        final String cookieLine = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=abcdefghijklmnopqrstuvwxyz";

        // when
        final HttpCookie cookie = new HttpCookie(cookieLine);
        final String actual = cookie.getJSessionId();

        // then
        final String expected = "abcdefghijklmnopqrstuvwxyz";
        assertThat(actual).isEqualTo(expected);
    }
}
