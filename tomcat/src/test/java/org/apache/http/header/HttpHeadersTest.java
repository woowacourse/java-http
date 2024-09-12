package org.apache.http.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.http.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Nested
    @DisplayName("문자열 배열로부터 HttpHeaders 객체 생성")
    class from {

        @Test
        @DisplayName("문자열 배열로부터 HttpHeaders 객체 생성 성공")
        void from() {
            final String[] headers = new String[]{
                    "Host: http://localhost:8080",
                    "Connection: keep-alive"
            };

            final HttpHeaders actual = new HttpHeaders(
                    new HttpHeader(HttpHeaderName.HOST, "http://localhost:8080"),
                    new HttpHeader(HttpHeaderName.CONNECTION, "keep-alive")
            );

            assertEquals(HttpHeaders.from(headers), actual);
        }

        @Test
        @DisplayName("문자열 배열로부터 HttpHeaders 객체 생성 실패: 존쟇라")
        void from_When() {
            final String[] headers = new String[]{
                    "Hoshttp://localhost:8080",
            };

            assertThatThrownBy(() -> HttpHeaders.from(headers))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("HttpHeader는 key value 쌍이여야 합니다.");
        }
    }

    @Test
    @DisplayName("지정된 형식으로 헤더 문자열 반환")
    void testToString() {
        final HttpHeaders headers = new HttpHeaders(
                new HttpHeader(HttpHeaderName.HOST, "http://localhost:8080"),
                new HttpHeader(HttpHeaderName.CONNECTION, "keep-alive")
        );

        assertEquals("Host: http://localhost:8080\r\nConnection: keep-alive\r\n", headers.toString());
    }

    @Test
    @DisplayName("이미 존재하는 헤더 이름인지 여부 확인")
    void existsHeader() {
        final HttpHeaders headers = new HttpHeaders(
                new HttpHeader(HttpHeaderName.HOST, "http://localhost:8080"),
                new HttpHeader(HttpHeaderName.CONNECTION, "keep-alive")
        );

        assertAll(
                () -> assertThat(headers.existsHeader(HttpHeaderName.HOST)).isTrue(),
                () -> assertThat(headers.existsHeader(HttpHeaderName.LOCATION)).isFalse()
        );
    }

    @Nested
    class getCookie {

        @Test
        @DisplayName("쿠키 조회 성공")
        void getCookie() {
            HttpHeaders httpHeaders = new HttpHeaders(
                    new HttpHeader(HttpHeaderName.HOST, "http://localhost:8080"),
                    new HttpHeader(HttpHeaderName.COOKIE, "JSESSIONID=1234")
            );
            assertThat(httpHeaders.getCookie()).isEqualTo(HttpCookie.from("JSESSIONID=1234"));
        }

        @Test
        @DisplayName("쿠키 조회 실패: 존재하지 않는 쿠키")
        void getCookieWhenCookieNotExist() {
            HttpHeaders httpHeaders = new HttpHeaders(
                    new HttpHeader(HttpHeaderName.HOST, "http://localhost:8080")
            );
            assertThatThrownBy(() -> httpHeaders.getCookie())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("쿠키가 존재하지 않습니다.");
        }
    }
}
