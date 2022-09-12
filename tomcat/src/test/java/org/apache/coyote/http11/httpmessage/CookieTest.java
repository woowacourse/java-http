package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.session.Cookie;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void cookie_데이터를_분리할_수_있다() {
        // given
        String cookieValue = "yummy_cookie=choco";

        // when
        Cookie cookie = Cookie.of(cookieValue);

        // then
        LinkedHashMap<String, Object> expected = new LinkedHashMap<>();
        expected.put("yummy_cookie", "choco");

        assertThat(cookie).extracting("cookies")
                .isEqualTo(expected);
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

        assertThat(cookie).extracting("cookies")
                .isEqualTo(expected);

    }

    @Test
    void cookie에_값이_존재하지_않을_때_빈_쿠키를_생성한다() {
        // given
        String cookieValue = "";
        // when
        Cookie cookie = Cookie.of(cookieValue);

        // then
        assertThat(cookie).extracting("cookies")
                .isEqualTo(Map.of());
    }
}
