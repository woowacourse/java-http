package org.apache.coyote.http11.protocol.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    @DisplayName("RequestHeaders 를 생성한다.")
    void create() {
        List<String> headerLines = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive"
        );

        RequestHeaders requestHeaders = new RequestHeaders(headerLines);

        assertAll(
                () -> assertThat(requestHeaders.getHeader("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(requestHeaders.getHeader("Connection")).isEqualTo("keep-alive")
        );
    }

    @Test
    @DisplayName(": 문자가 없는 헤더는 무시한다.")
    void ignoreHeaderWithoutColon() {
        List<String> headerLines = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Skip"
        );

        RequestHeaders requestHeaders = new RequestHeaders(headerLines);

        assertAll(
                () -> assertThat(requestHeaders.getHeader("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(requestHeaders.getHeader("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(requestHeaders.getHeader("Skip")).isNull()
        );
    }

    @Test
    @DisplayName("빈 헤더 리스트를 처리한다.")
    void createWithEmptyHeaders() {
        List<String> headerLines = List.of();  // 빈 리스트

        RequestHeaders requestHeaders = new RequestHeaders(headerLines);

        assertThat(requestHeaders.getHeaders()).isEmpty();
    }

    @Test
    @DisplayName("쿠키 헤더를 가져온다.")
    void getCookieHeader() {
        List<String> headerLines = List.of(
                "Host: localhost:8080",
                "Cookie: sessionId=abc123"
        );

        RequestHeaders requestHeaders = new RequestHeaders(headerLines);

        assertThat(requestHeaders.getCookieString()).isEqualTo("sessionId=abc123");
    }

    @Test
    @DisplayName("잘못된 형식의 헤더는 무시한다.")
    void ignoreInvalidHeader() {
        List<String> headerLines = List.of(
                "Host: localhost:8080",
                "InvalidHeaderWithoutColon"
        );

        RequestHeaders requestHeaders = new RequestHeaders(headerLines);

        assertThat(requestHeaders.getHeader("InvalidHeaderWithoutColon")).isNull();  // 무시됨
    }
}
