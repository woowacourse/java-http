package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @DisplayName("HttpCookies는 문자열로 생성 시 쿠키 리스트로 파싱된다.")
    @Test
    void constructorWithString() {
        // given
        String cookiesString = "sessionId=abc123; userId=xyz456";

        // when
        HttpCookies httpCookies = new HttpCookies(cookiesString);

        // then
        List<HttpCookie> expectedCookies = List.of(
                new HttpCookie("sessionId", "abc123"),
                new HttpCookie("userId", "xyz456")
        );

        assertThat(httpCookies.getCookie("sessionId")).isEqualTo(expectedCookies.get(0));
        assertThat(httpCookies.getCookie("userId")).isEqualTo(expectedCookies.get(1));
    }

    @DisplayName("getCookie() 메서드는 주어진 이름의 쿠키를 반환한다.")
    @Test
    void getCookie() {
        // given
        HttpCookie sessionCookie = new HttpCookie("sessionId", "abc123");
        HttpCookie userCookie = new HttpCookie("userId", "xyz456");
        List<HttpCookie> cookiesList = List.of(sessionCookie, userCookie);
        HttpCookies httpCookies = new HttpCookies(cookiesList);

        // when
        HttpCookie cookie = httpCookies.getCookie("sessionId");

        // then
        assertThat(cookie.getName()).isEqualTo("sessionId");
        assertThat(cookie.getValue()).isEqualTo("abc123");
    }

    @DisplayName("getCookie() 메서드는 주어진 이름의 쿠키가 없을 경우 빈 쿠키를 반환한다.")
    @Test
    void getCookieNotFound() {
        // given
        HttpCookie sessionCookie = new HttpCookie("sessionId", "abc123");
        List<HttpCookie> cookiesList = List.of(sessionCookie);
        HttpCookies httpCookies = new HttpCookies(cookiesList);

        // when
        HttpCookie cookie = httpCookies.getCookie("nonExistingCookie");

        // then
        assertThat(cookie.getName()).isEqualTo("nonExistingCookie");
        assertThat(cookie.getValue()).isEmpty();
    }

    @DisplayName("toString() 메서드는 쿠키 리스트를 문자열로 반환한다.")
    @Test
    void toStringTest() {
        // given
        HttpCookie sessionCookie = new HttpCookie("sessionId", "abc123");
        HttpCookie userCookie = new HttpCookie("userId", "xyz456");
        List<HttpCookie> cookiesList = List.of(sessionCookie, userCookie);
        HttpCookies httpCookies = new HttpCookies(cookiesList);

        // when
        String result = httpCookies.toString();

        // then
        assertThat(result).isEqualTo("sessionId=abc123; userId=xyz456");
    }
}
