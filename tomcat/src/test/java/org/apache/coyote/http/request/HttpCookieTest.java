package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import common.HttpConstants;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("쿠키 파싱 - 기본 케이스")
    void parse_basic() {
        // given
        final String raw = "JSESSIONID=ABC123; theme=dark; lang=ko";

        // when
        final HttpCookie cookie = HttpCookie.from(raw);

        // then
        assertSoftly(softly -> {
            softly.assertThat(cookie.get("JSESSIONID")).isEqualTo("ABC123");
            softly.assertThat(cookie.get("theme")).isEqualTo("dark");
            softly.assertThat(cookie.get("lang")).isEqualTo("ko");
        });
    }

    @Test
    @DisplayName("쿠키 파싱 - null/빈 문자열은 비어있는 쿠키")
    void parse_nullOrEmpty() {
        // when & then
        assertSoftly(softly -> {
            softly.assertThat(HttpCookie.from(null).toString()).isEqualTo("");
            softly.assertThat(HttpCookie.from("").toString()).isEqualTo("");
        });
    }

    @Test
    @DisplayName("쿠키 라인에 '=' 누락 시 예외")
    void parse_missingEquals_throws() {
        assertThatThrownBy(() -> HttpCookie.from("invalidpair"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠키 형식이 올바르지 않습니다");
    }

    @Test
    @DisplayName("toString은 Cookie: 헤더 형식으로 반환하고 순서는 무시")
    void toString_cookieHeaderFormat_orderAgnostic() {
        // given
        final HttpCookie cookie = HttpCookie.from("a=1; b=2");

        // when
        final String s = cookie.toString();
        final String payload = s.substring((HttpConstants.COOKIE_HEADER_NAME + ": ").length());
        final String[] parts = payload.split(";");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        // then
        assertSoftly(softly -> {
            softly.assertThat(s)
                    .startsWith(HttpConstants.COOKIE_HEADER_NAME + HttpConstants.HEADER_VALUE_SEPARATOR + " ");
            softly.assertThat(Arrays.asList(parts)).containsExactlyInAnyOrder("a=1", "b=2");
        });
    }
}
