package org.apache.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("RequestLine 문자열로부터 객체 파싱 성공")
    void from() {
        RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }

    @Test
    @DisplayName("RequestLine 문자열로부터 객체 실패: 3개의 형식으로 이루어져 있지 않은 경우")
    void from_When_Invalid_PartSize() {
        String invalidRawRequestLine = "GET HTTP/1.1";

        assertThatThrownBy(() -> RequestLine.from(invalidRawRequestLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("RequestLine 형식이 맞지 않습니다. method, path, version으로 구성해주세요.");
    }

    @Test
    @DisplayName("공백 문자를 기준으로 반환 성공")
    void testToString() {
        String requestLine = "GET /index.html HTTP/1.1";
        assertThat(RequestLine.from(requestLine)).hasToString(requestLine);
    }
}
