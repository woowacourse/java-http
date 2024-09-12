package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    @DisplayName("쿠키 이름을 반환해야 한다")
    void testGetName() {
        Cookie cookie = Cookie.of("key", "value");

        assertThat(cookie.getName()).isEqualTo("key");
    }

    @Test
    @DisplayName("쿠키 값을 반환해야 한다")
    void testGetValue() {
        Cookie cookie = Cookie.of("key", "value");

        assertThat(cookie.getValue()).isEqualTo("value");
    }

    @Test
    @DisplayName("쿠키 문자열을 생성해야 한다")
    void testGetCookieString() {
        Cookie cookie = Cookie.of("key", "value");

        assertThat(cookie.getCookieString()).isEqualTo("key=value");
    }

    @Test
    @DisplayName("세션 쿠키를 생성해야 한다")
    void testSession() {
        Cookie sessionCookie = Cookie.session("abc123");

        assertAll(
                () -> assertThat(sessionCookie.getName()).isEqualTo("JSESSIONID"),
                () -> assertThat(sessionCookie.getValue()).isEqualTo("abc123")
        );
    }
}
