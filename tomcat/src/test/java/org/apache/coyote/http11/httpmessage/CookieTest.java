package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void cookie를_생성할_수_있다() {
        // given
        final String key = "JSESSIONID";
        final String value = "1234";

        // when
        Cookie cookie = new Cookie(Map.of(key, value));

        // then
        assertThat(cookie.getCookies()).isEqualTo(Map.of(key, value));
    }
    @Test
    void cookie_데이터를_분리할_수_있다() {
        // given
        String cookieValue = "yummy_cookie=choco";

        // when
        Cookie cookie = Cookie.of(cookieValue);

        // then
        LinkedHashMap<String, Object> expected = new LinkedHashMap<>();
        expected.put("yummy_cookie", "choco");

        assertThat(cookie.getCookies()).isEqualTo(expected);
    }

    @Test
    void cookie_데이터가_여러개일_떄_분리할_수_있다() {
        // given
        String cookieValue = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        Cookie cookie = Cookie.of(cookieValue);

        // then
        LinkedHashMap<String, Object> expected = new LinkedHashMap<>();
        expected.put("yummy_cookie", "choco");
        expected.put("tasty_cookie", "strawberry");
        expected.put("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(cookie.getCookies()).isEqualTo(expected);
    }

    @Test
    void cookie를_출력할_수_있다() {
        // given
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("JSESSIONID", "1234");
        cookies.put("name", "park");

        // when
        Cookie cookie = new Cookie(cookies);

        // then
        assertThat(cookie.toString()).isEqualTo("JSESSIONID=1234; name=park ");
    }

    @Test
    void cookie에_값이_존재하지_않을_때_빈_쿠키를_생성한다() {
        // given
        String cookieValue = "";
        // when
        Cookie cookie = Cookie.of(cookieValue);

        // then
        assertThat(cookie.getCookies()).isEqualTo(Map.of());
    }
}
