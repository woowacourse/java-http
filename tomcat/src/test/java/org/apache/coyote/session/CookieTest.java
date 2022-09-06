package org.apache.coyote.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @DisplayName("비어있는 쿠키를 생성하고 검사할 수 있다.")
    @Test
    void emptyCookie() {
        //given
        final Cookie emptyCookie = Cookie.empty();

        //when
        final boolean result = emptyCookie.isEmpty();

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("키 값이 동일한지를 검사할 수 있다.")
    @Test
    void isSameKey() {
        //given
        final Cookie cookie = new Cookie("key", "value");

        //when
        final boolean result = cookie.isSameKey("key");
        final boolean invalidResult = cookie.isSameKey("invalid");

        //then
        assertThat(result).isTrue();
        assertThat(invalidResult).isFalse();
    }

    @DisplayName("헤더 형식으로 쿠키를 반환할 수 있다.")
    @Test
    void toHeaderFormat() {
        //given
        final Cookie cookie = new Cookie("key", "value");

        //when
        final String result = cookie.toHeaderFormat();

        //then
        assertThat(result).isEqualTo("key=value");
    }
}
