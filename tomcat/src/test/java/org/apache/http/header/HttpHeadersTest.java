package org.apache.http.header;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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
            String[] headers = new String[] {
                    "Host: http://localhost:8080",
                    "Connection: keep-alive"
            };
            HttpHeaders actual = new HttpHeaders(new HttpHeader[]{
                    new HttpHeader("Host", "http://localhost:8080"),
                    new HttpHeader("Connection", "keep-alive")
            });

            assertEquals(HttpHeaders.from(headers), actual);
        }

        @Test
        @DisplayName("문자열 배열로부터 HttpHeaders 객체 생성 실패: 존쟇라")
        void from_When() {
            String[] headers = new String[] {
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
        HttpHeaders headers = new HttpHeaders(new HttpHeader[]{
                new HttpHeader("Host", "http://localhost:8080"),
                new HttpHeader("Connection", "keep-alive")
        });

        assertEquals("Host: http://localhost:8080\r\nConnection: keep-alive\r\n", headers.toString());
    }
}
