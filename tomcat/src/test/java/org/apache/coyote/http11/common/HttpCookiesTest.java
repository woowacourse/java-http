package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @Test
    @DisplayName("쿠키를 추가할 수 있어야 한다")
    void testSetCookie() {
        HttpCookies httpCookies = new HttpCookies();

        httpCookies.setCookie("key", "value");

        assertThat(httpCookies.get("key")).isEqualTo("value");
    }

    @Test
    @DisplayName("쿠키를 제거할 수 있어야 한다")
    void testRemoveCookie() {
        HttpCookies httpCookies = new HttpCookies();
        httpCookies.setCookie("key", "value");

        httpCookies.removeCookie("key");

        assertThat(httpCookies.get("key")).isNull();
    }

    @Test
    @DisplayName("쿠키 문자열을 생성할 수 있어야 한다")
    void testGetCookieString() {
        HttpCookies httpCookies = new HttpCookies();
        httpCookies.setCookie("key", "value");
        httpCookies.setCookie("userId", "67890");

        String cookieString = httpCookies.getCookieString();

        assertThat(cookieString).contains("key=value", "userId=67890");
    }

    @Test
    @DisplayName("쿠키가 포함되어 있는지 확인할 수 있어야 한다")
    void testContains() {
        HttpCookies httpCookies = new HttpCookies();
        httpCookies.setCookie("key", "value");

        assertAll(
                () -> assertThat(httpCookies.contains("key")).isTrue(),
                () -> assertThat(httpCookies.contains("userId")).isFalse()
        );
    }

    @Test
    @DisplayName("문자열로부터 쿠키를 생성할 수 있어야 한다")
    void testFromString() {
        String cookieString = "key=value; userId=67890";

        HttpCookies cookiesFromString = HttpCookies.from(cookieString);

        assertAll(
                () -> assertThat(cookiesFromString.get("key")).isEqualTo("value"),
                () -> assertThat(cookiesFromString.get("userId")).isEqualTo("67890")
        );
    }

    @Test
    @DisplayName("잘못된 형식의 쿠키 문자열을 처리할 수 있어야 한다")
    void testFromInvalidString() {
        String invalidCookieString = "invalidCookieString";

        assertThatThrownBy(() -> HttpCookies.from(invalidCookieString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 형식의 쿠키 문자열입니다.");
    }
}
