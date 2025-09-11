package org.apache.catalina.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.spring.http.cookie.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class HttpCookieTest {

    @DisplayName("toString 메서드: name=value 형식의 문자열을 반환한다")
    @Test
    void toStringTest1() {
        // given
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");

        // when
        String result = cookie.toString();

        // then
        assertThat(result).isEqualTo("sessionId=abc123");
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

    @DisplayName("쿠키 이름이 null인 경우 예외 발생")
    @ParameterizedTest
    @NullAndEmptySource
    void cookieNameNull(String name) {
        // when & then
        assertThatThrownBy(() -> new HttpCookie(name, "value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cookie name cannot be null or empty");
    }

    @DisplayName("쿠키 값이 null인 경우 예외 발생")
    @Test
    void cookieValueNull() {
        // when & then
        assertThatThrownBy(() -> new HttpCookie("name", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cookie value cannot be null");
    }

}
