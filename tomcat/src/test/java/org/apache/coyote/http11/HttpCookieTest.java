package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("여러 쿠키를 파싱한다")
    void parse() {
        HttpCookie cookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(cookie.getValue("yummy_cookie")).contains("choco");
        assertThat(cookie.getValue("tasty_cookie")).contains("strawberry");
        assertThat(cookie.getValue("JSESSIONID")).contains("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @Test
    @DisplayName("세미콜론 뒤에 공백이 없어도 파싱한다")
    void parseWithoutSpace() {
        HttpCookie cookie = HttpCookie.from("yummy_cookie=choco;tasty_cookie=strawberry");

        assertThat(cookie.getValue("yummy_cookie")).contains("choco");
        assertThat(cookie.getValue("tasty_cookie")).contains("strawberry");
    }

    @Test
    @DisplayName("JSESSIONID가 있으면 참을 반환한다")
    void hasJSessionId() {
        HttpCookie cookie = HttpCookie.from("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(cookie.hasJSessionId()).isTrue();
    }

    @Test
    @DisplayName("JSESSIONID가 없으면 거짓을 반환한다")
    void hasNoJSessionId() {
        HttpCookie cookie = HttpCookie.from("yummy_cookie=choco");

        assertThat(cookie.hasJSessionId()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 쿠키를 조회하면 빈 값을 반환한다")
    void notFound() {
        HttpCookie cookie = HttpCookie.from("yummy_cookie=choco");

        assertThat(cookie.getValue("unknown")).isEmpty();
    }

    @Test
    @DisplayName("쿠키 헤더가 없으면 빈 쿠키를 반환한다")
    void nullHeader() {
        HttpCookie cookie = HttpCookie.from(null);

        assertThat(cookie.hasJSessionId()).isFalse();
        assertThat(cookie.getValue("anything")).isEmpty();
    }
}
