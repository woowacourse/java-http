package nextstep.org.apache.coyote.http11.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.http11.web.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CookieTest {

    @DisplayName("쿠키 키와 값이 같으면 같은 쿠키다.")
    @Test
    void cookie() {
        // given
        final Cookie cookie1 = new Cookie("JSESSIONID", "value");
        final Cookie cookie2 = new Cookie("JSESSIONID", "value");

        // when, then
        assertEquals(cookie1, cookie2);
    }

    @DisplayName("쿠키 키와 값을 문자열로 바꾼다.")
    @Test
    void toPair() {
        // given
        final Cookie cookie = new Cookie("JSESSIONID", "value");

        // when
        final String value = cookie.toPair();

        // then
        assertThat(value).isEqualTo("JSESSIONID=value");
    }
}
