package org.apache.catalina.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.domain.cookie.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("toCookieString 메서드: name=value 형식의 문자열을 반환한다")
    @Test
    void toCookieString() {
        // given
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");

        // when
        String result = cookie.toCookieString();

        // then
        assertThat(result).isEqualTo("sessionId=abc123");
    }

    @DisplayName("toString 메서드: toCookieString과 동일한 결과를 반환한다")
    @Test
    void toString_sameAsToCookieString() {
        // given
        HttpCookie cookie = new HttpCookie("userId", "456");

        // when & then
        assertThat(cookie.toString()).hasToString(cookie.toCookieString());
        assertThat(cookie.toString()).hasToString("userId=456");
    }

    @DisplayName("sameName 메서드: 동일한 이름의 쿠키인지 확인한다")
    @Test
    void sameName_sameNames() {
        // given
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");

        // when & then
        assertThat(cookie.sameName("sessionId")).isTrue();
        assertThat(cookie.sameName("otherId")).isFalse();
    }

    @DisplayName("sameName 메서드: 대소문자를 구분한다")
    @Test
    void sameName_caseSensitive() {
        // given
        HttpCookie cookie = new HttpCookie("SessionId", "abc123");

        // when & then
        assertThat(cookie.sameName("SessionId")).isTrue();
        assertThat(cookie.sameName("sessionid")).isFalse();
        assertThat(cookie.sameName("SESSIONID")).isFalse();
    }

    @DisplayName("빈 값을 가진 쿠키 생성")
    @Test
    void createCookieWithEmptyValue() {
        // given
        HttpCookie cookie = new HttpCookie("emptyCookie", "");

        // when & then
        assertThat(cookie.name()).hasToString("emptyCookie");
        assertThat(cookie.value()).hasToString("");
        assertThat(cookie.toString()).hasToString("emptyCookie=");
    }

    @DisplayName("특수 문자를 포함한 쿠키 값")
    @Test
    void cookieWithSpecialCharacters() {
        // given
        HttpCookie cookie = new HttpCookie("specialCookie", "value=with&special%chars");

        // when & then
        assertThat(cookie.toString()).hasToString("specialCookie=value=with&special%chars");
    }
}
