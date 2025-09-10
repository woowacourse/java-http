package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpCookieTest {

    @DisplayName("쿠키를 파싱할 수 있다.")
    @Test
    void parse() {
        // given
        final var cookieHeader = "JSESSIONID=1234abcd5678efgh; lang=ko; Path=/";
        final var httpCookie = new HttpCookie(cookieHeader);

        // when
        final var jSessionId = httpCookie.getCookie("JSESSIONID");
        final var lang = httpCookie.getCookie("lang");
        final var path = httpCookie.getCookie("Path");

        // then
        assertAll(
                () -> {
                    assertThat(jSessionId).isPresent();
                    assertThat(jSessionId.get()).isEqualTo("1234abcd5678efgh");
                },
                () -> {
                    assertThat(lang).isPresent();
                    assertThat(lang.get()).isEqualTo("ko");
                },
                () -> {
                    assertThat(path).isPresent();
                    assertThat(path.get()).isEqualTo("/");
                }
        );
    }

    @DisplayName("쿠키가 없으면 빈 맵을 가진다.")
    @Test
    void no_cookie() {
        // given
        final var httpCookie = new HttpCookie(null);

        // when
        final var jSessionId = httpCookie.getCookie("JSESSIONID");

        // then
        assertThat(jSessionId).isNotPresent();
    }

    @DisplayName("쿠키 값에 = 문자가 포함될 수 있다.")
    @Test
    void cookie_value_with_equal_sign() {
        // given
        final var cookieHeader = "token=abc=def==; lang=ko";
        final var httpCookie = new HttpCookie(cookieHeader);

        // when
        final var token = httpCookie.getCookie("token");
        final var lang = httpCookie.getCookie("lang");

        // then
        assertAll(
                () -> {
                    assertThat(token).isPresent();
                    assertThat(token.get()).isEqualTo("abc=def==");
                },
                () -> {
                    assertThat(lang).isPresent();
                    assertThat(lang.get()).isEqualTo("ko");
                }
        );
    }

    @DisplayName("다양한 공백 형태를 가진 쿠키 헤더를 파싱할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "auth=token;dark_mode=true",      // 공백 없음
            "auth=token; dark_mode=true",     // 일반적인 공백 1개
            "auth=token;  dark_mode=true",    // 공백 여러 개
            " auth=token ;  dark_mode=true ", // 각 쌍의 앞뒤 공백
            "auth=token;dark_mode=true;"      // 마지막 세미콜론
    })
    void parse_handles_various_whitespace(final String cookieHeader) {
        // given
        final var httpCookie = new HttpCookie(cookieHeader);

        // when
        final var auth = httpCookie.getCookie("auth");
        final var darkMode = httpCookie.getCookie("dark_mode");

        // then
        assertAll(
                () -> {
                    assertThat(auth).isPresent();
                    assertThat(auth.get()).isEqualTo("token");
                },
                () -> {
                    assertThat(darkMode).isPresent();
                    assertThat(darkMode.get()).isEqualTo("true");
                }
        );
    }
}
