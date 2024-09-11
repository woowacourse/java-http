package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @DisplayName("쿠키를 키, 값으로 나누어 저장한다.")
    @Test
    void createHttpCookies() {
        HttpCookies cookies =
                new HttpCookies("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        Map<String, String> expected = Map.of(
                "yummy_cookie", "choco",
                "tasty_cookie", "strawberry",
                "JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        assertThat(cookies.getCookies())
                .containsExactlyInAnyOrderEntriesOf(expected);
    }

    @DisplayName("JSESSIONID 키가 있으면 true를 반환한다.")
    @Test
    void hasJSESSIONID() {
        HttpCookies cookies =
                new HttpCookies("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertTrue(cookies.hasJSESSIONID());
    }

    @DisplayName("JSESSIONID 키가 없으면 false를 반환한다.")
    @Test
    void hasJSESSIONIDWithoutJSESSIONID() {
        HttpCookies cookies =
                new HttpCookies("yummy_cookie=choco");

        assertFalse(cookies.hasJSESSIONID());
    }

    @DisplayName("저장된 JSESSIONID를 반환한다.")
    @Test
    void getJSESSIONID() {
        String JSESSIONID = "656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpCookies cookies =
                new HttpCookies("JSESSIONID=" + JSESSIONID);

        assertThat(cookies.getJSESSIONID()).isEqualTo(JSESSIONID);
    }
}
