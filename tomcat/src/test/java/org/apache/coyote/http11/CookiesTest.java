package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
