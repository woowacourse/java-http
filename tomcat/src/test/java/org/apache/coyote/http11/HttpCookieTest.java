package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("HttpCookie는 기본 생성자로 생성되면 빈 이름과 값으로 초기화된다.")
    @Test
    void testDefaultConstructor() {
        // given
        HttpCookie cookie = new HttpCookie();

        // when
        String name = cookie.getName();
        String value = cookie.getValue();

        // then
        assertAll(
                () -> assertThat(name).isEmpty(),
                () -> assertThat(value).isEmpty()
        );
    }

    @DisplayName("HttpCookie는 이름과 값으로 생성되면 해당 값이 설정된다.")
    @Test
    void testConstructorWithNameAndValue() {
        // given
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");

        // when
        String name = cookie.getName();
        String value = cookie.getValue();

        // then
        assertAll(
                () -> assertThat(name).isEqualTo("sessionId"),
                () -> assertThat(value).isEqualTo("abc123")
        );
    }

    @DisplayName("setName() 메서드는 쿠키의 이름을 설정한다.")
    @Test
    void testSetName() {
        // given
        HttpCookie cookie = new HttpCookie();
        cookie.setName("sessionId");

        // when
        String name = cookie.getName();

        // then
        assertThat(name).isEqualTo("sessionId");
    }

    @DisplayName("setValue() 메서드는 쿠키의 값을 설정한다.")
    @Test
    void testSetValue() {
        // given
        HttpCookie cookie = new HttpCookie();
        cookie.setValue("abc123");

        // when
        String value = cookie.getValue();

        // then
        assertThat(value).isEqualTo("abc123");
    }

    @DisplayName("setHttpOnly() 메서드는 HttpOnly 플래그를 설정한다.")
    @Test
    void testSetHttpOnly() {
        // given
        HttpCookie cookie = new HttpCookie();
        cookie.setHttpOnly(true);

        // when
        String result = cookie.toString();

        // then
        assertThat(result).isEqualTo("=; HttpOnly");
    }

    @DisplayName("toString() 메서드는 쿠키의 이름과 값, 그리고 HttpOnly 플래그를 포함한다.")
    @Test
    void toStringTest() {
        // given
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");
        cookie.setHttpOnly(true);

        // when
        String result = cookie.toString();

        // then
        assertThat(result).isEqualTo("sessionId=abc123; HttpOnly");
    }
}
