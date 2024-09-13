package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void 쿠키_생성() {
        // given
        String cookieValue = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie cookie = new HttpCookie(cookieValue);

        // then
        Map<String, String> actual = cookie.getCookies();

        assertAll(
                () -> assertThat(actual).hasSize(3),
                () -> assertThat(actual).containsEntry("yummy_cookie", "choco"),
                () -> assertThat(actual).containsEntry("tasty_cookie", "strawberry"),
                () -> assertThat(actual).containsEntry("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @Test
    void 쿠키값이_없는_경우_비어있는_쿠키_생성() {
        // given
        String cookieValue = "";

        // when
        HttpCookie cookie = new HttpCookie(cookieValue);

        // then
        Map<String, String> actual = cookie.getCookies();

        assertThat(actual).isEmpty();
    }

    @Test
    void 쿠키값이_null인_경우_비어있는_쿠키_생성() {
        // given
        String cookieValue = null;

        // when
        HttpCookie cookie = new HttpCookie(cookieValue);

        // then
        Map<String, String> actual = cookie.getCookies();

        assertThat(actual).isEmpty();
    }

    @Test
    void JSessionId로_쿠키_생성() {
        // given
        String JSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie cookie = HttpCookie.ofJSessionId(JSessionId);

        // then
        Map<String, String> actual = cookie.getCookies();

        assertThat(actual).hasSize(1).containsEntry("JSESSIONID", JSessionId);
    }
}
