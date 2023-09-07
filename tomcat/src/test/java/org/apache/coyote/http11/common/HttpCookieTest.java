package org.apache.coyote.http11.common;

import org.apache.coyote.http11.common.header.HttpCookie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @Test
    @DisplayName("쿠키 문자열을 파싱하여 저장한다.")
    void stringToHttpCookie() {
        // given
        final String cookieString = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        final HttpCookie actual = HttpCookie.from(cookieString);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getCookie()).hasSize(3);
            softAssertions.assertThat(actual.getCookie().get("yummy_cookie")).isEqualTo("choco");
            softAssertions.assertThat(actual.getCookie().get("tasty_cookie")).isEqualTo("strawberry");
            softAssertions.assertThat(actual.getCookie().get("JSESSIONID"))
                          .isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        });
    }

    @Test
    @DisplayName("쿠키가 비어있으면 true를 반환한다.")
    void isEmpty_true() {
        // given
        final HttpCookie httpCookie = new HttpCookie(null);

        // when
        final boolean actual = httpCookie.isEmpty();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("쿠키가 존재하면 false를 반환한다.")
    void isEmpty_false() {
        // given
        final HttpCookie httpCookie = new HttpCookie(Map.of("JSESSION", "1234567890"));

        // when
        final boolean actual = httpCookie.isEmpty();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("쿠키값을 반환한다.")
    void search() {
        // given
        final String cookieKey = "JSESSION";
        final String cookieValue = "1234567890";
        final HttpCookie httpCookie = new HttpCookie(Map.of(cookieKey, cookieValue));

        // when
        final String actual = httpCookie.search(cookieKey);

        // then
        assertThat(actual).isEqualTo(cookieValue);
    }

    @Test
    @DisplayName("주어진 HttpCookie를 문자열로 변환한다.")
    void httpCookieToString() {
        // given
        final String cookieString = "yummy_cookie=choco";
        final HttpCookie httpCookie = HttpCookie.from(cookieString);

        // when
        final String actual = httpCookie.convertToString();

        // then
        assertThat(actual).isEqualTo(cookieString);
    }
}
