package org.apache.coyote.http11;

import org.apache.coyote.http11.cookie.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class CookiesTest {
    final String line = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
    final Cookies cookies = Cookies.from(line);

    @Test
    @DisplayName("각 쿠키는 ; 로 구분한다.")
    void seperate_each_query_with_semicolon_and_space() {

        final String cookie1 = cookies.getCookie("yummy_cookie");
        final String cookie2 = cookies.getCookie("JSESSIONID");
        assertThat(cookie1).isNotNull();
        assertThat(cookie2).isNotNull();
    }

    @Test
    @DisplayName("쿼리는 =를 기반으로 키와 값을 분류한다.")
    void seperate_query_with_ampersand() {
        final String cookie1 = cookies.getCookie("yummy_cookie");
        final String cookie2 = cookies.getCookie("JSESSIONID");
        assertThat(cookie1).isEqualTo("choco");
        assertThat(cookie2).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @Test
    @DisplayName("빈 값과 문자열을 넣으면 기본으로 생성한다.")
    void null_line_throw_exception() {
        assertThatCode(() -> {
            Cookies.from(null);
            Cookies.from("");
        }).doesNotThrowAnyException();

        assertThat(Cookies.from(null)).isEqualTo(new Cookies(Map.of()));
    }

    @Test
    @DisplayName("잘못된 문법으로 넣으면 예외를 반환한다.")
    void informal_line_throw_exception() {
        assertThatThrownBy(() -> Cookies.from("yummy_cookie!choco; tasty_cookie<strawberry;"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
