package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpCookie 테스트")
class HttpCookiesTest {

    private HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
    }

    @Test
    @DisplayName("쿠키에 값을 추가하고 조회할 수 있다")
    void putAndGetCookie() {
        // given
        HttpCookies httpCookies = new HttpCookies();

        // when
        httpCookies.putCookie("token", "xyz789");

        // then
        assertThat(httpCookies.getCookieValue("token")).isEqualTo("xyz789");
    }

    @Test
    @DisplayName("헤더에 쿠키가 여러 개 있어도 각각 올바르게 파싱된다.")
    void parseMultipleCookies() {
        // given
        httpHeaders.putHeader(HttpHeaderName.COOKIE, "theme=dark; sessionToken=abc456; locale=en-US");

        // when
        HttpCookies httpCookies = HttpCookies.from(httpHeaders);

        // then
        Assertions.assertAll(
                () -> assertThat(httpCookies.getCookieValue("theme")).isEqualTo("dark"),
                () -> assertThat(httpCookies.getCookieValue("sessionToken")).isEqualTo("abc456"),
                () -> assertThat(httpCookies.getCookieValue("locale")).isEqualTo("en-US")
        );
    }
}
