package org.apache.coyote.http11.header;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("쿠키")
class CookieTest {

    @Test
    @DisplayName("여러 쿠키를 이름과 값으로 파싱한다")
    void parsesMultipleCookies() {
        // given
        final String cookieHeader = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=abc-123";

        // when
        final Cookie cookie = Cookie.from(cookieHeader);

        // then
        assertThat(cookie.get("yummy_cookie")).contains("choco");
        assertThat(cookie.get("tasty_cookie")).contains("strawberry");
        assertThat(cookie.get("JSESSIONID")).contains("abc-123");
    }

    @Test
    @DisplayName("쿠키 헤더가 없으면 비어 있는 쿠키를 반환한다")
    void returnsEmptyCookieWhenHeaderIsMissing() {
        // when
        final Cookie cookie = Cookie.from(null);

        // then
        assertThat(cookie.get("JSESSIONID")).isEmpty();
    }

    @Test
    @DisplayName("잘못된 쿠키 조각은 무시하고 정상 쿠키를 파싱한다")
    void ignoresMalformedCookieToken() {
        // given
        final String cookieHeader = "yummy_cookie=choco; broken-cookie; JSESSIONID=abc-123";

        // when
        final Cookie cookie = Cookie.from(cookieHeader);

        // then
        assertThat(cookie.get("yummy_cookie")).contains("choco");
        assertThat(cookie.get("JSESSIONID")).contains("abc-123");
    }

    @Test
    @DisplayName("쿠키 값에 포함된 등호를 유지한다")
    void preservesEqualsSignsInCookieValue() {
        // when
        final Cookie cookie = Cookie.from("token=abc==");

        // then
        assertThat(cookie.get("token")).contains("abc==");
    }
}
