package org.apache.coyote.http11.protocol.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    @DisplayName("쿠키를 조회한다.")
    void getCookie() {
        Cookie cookie = new Cookie();
        cookie.setValue("key1", "value1");

        String value1 = cookie.getValue("key1");

        assertThat(value1).isEqualTo("value1");
    }

    @Test
    @DisplayName("쿠키가 존재하는지 확인한다.")
    void containsCookie() {
        Cookie cookie = new Cookie();
        cookie.setValue("key1", "value1");

        boolean isContains = cookie.containsKey("key1");

        assertThat(isContains).isTrue();
    }

    @Test
    @DisplayName("쿠키를 문자로 변환한다.")
    void toCookieString() {
        Cookie cookie = new Cookie();
        cookie.setValue("key1", "value1");

        String cookieString = cookie.toCookieString();
        assertThat(cookieString).isEqualTo("key1=value1");
    }

    @Test
    @DisplayName("빈 쿠키 문자열을 처리한다.")
    void parseEmptyCookieString() {
        Cookie cookie = new Cookie("");

        assertThat(cookie.toCookieString()).isEmpty();
    }

    @Test
    @DisplayName("잘못된 형식의 쿠키 문자열을 처리한다.")
    void parseInvalidCookieString() {
        Cookie cookie = new Cookie("invalidcookie");

        assertThat(cookie.toCookieString()).isEmpty();
    }

    @Test
    @DisplayName("쿠키 문자열에 값이 없는 경우 무시한다.")
    void parseCookieWithNoValue() {
        Cookie cookie = new Cookie("key1=");

        assertThat(cookie.containsKey("key1")).isFalse();
    }

    @Test
    @DisplayName("중복된 쿠키 키가 있을 때 마지막 값을 가져온다.")
    void parseDuplicateKeys() {
        Cookie cookie = new Cookie("key1=value1; key1=value2");

        assertThat(cookie.getValue("key1")).isEqualTo("value2");
    }

    @Test
    @DisplayName("null 쿠키 문자열을 처리할 때 예외가 발생하지 않는다.")
    void parseNullCookieString() {
        Cookie cookie = new Cookie(null);

        assertThat(cookie.toCookieString()).isEmpty();
    }
}
