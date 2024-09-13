package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import org.apache.coyote.http11.exception.HttpFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpHeadersTest {

    @DisplayName("문자열을 파싱하여 헤더를 생성한다.")
    @Test
    void createHttpHeaders() {
        List<String> rawHeaders = List.of(
                "Content-Length: 10",
                "Accept: text/html"
        );

        HttpHeaders headers = HttpHeaders.of(rawHeaders);

        assertAll(
                () -> assertThat(headers.get("Content-Length")).isEqualTo("10"),
                () -> assertThat(headers.get("Accept")).isEqualTo("text/html")
        );
    }

    @DisplayName("세션 쿠키를 추가한다.")
    @Test
    void addSessionId() {
        String sessionId = "1234";
        HttpHeaders headers = new HttpHeaders(new HashMap<>());

        headers.addSession(sessionId);

        assertThat(headers.getCookie("JSESSIONID")).isEqualTo("1234");
    }

    @DisplayName("쿠키에 저장된 세션 아이디를 반환한다.")
    @Test
    void getSessionId() {
        List<String> rawHeaders = List.of(
                "Cookie: JSESSIONID=1234"
        );

        HttpHeaders headers = HttpHeaders.of(rawHeaders);

        assertThat(headers.getSessionId()).isEqualTo("1234");
    }

    @DisplayName("올바르지 않은 헤더 형식일 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"hello: ", "", ": value", "hello: : bye"})
    void invalidHeaders(String header) {
        assertThatThrownBy(() -> HttpHeaders.of(List.of(header)))
                .isInstanceOf(HttpFormatException.class)
                .hasMessage("올바르지 않은 헤더 형식입니다.");
    }
}
