package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestTest {

    @DisplayName("Http1.1에 적합하면 생성한다.")
    @Test
    void create() {
        assertThatCode(() -> new Http11Request(
                new Http11RequestLine("GET /path HTTP/1.1"),
                new Http11RequestHeaders(List.of("Host: localhost::8080")),
                Http11RequestBody.ofEmpty())
        ).doesNotThrowAnyException();
    }

    @DisplayName("RequestLine이 HTTP 1.1 스펙에 부적합하면 예외가 발생한다.")
    @Test
    void create_Fail1() {
        assertThatThrownBy(() -> new Http11Request(
                new Http11RequestLine("GET/pathHTTP/1.1"),
                new Http11RequestHeaders(List.of("Host: localhost::8080")),
                Http11RequestBody.ofEmpty())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 HTTP RequestLine이 아닙니다.");
    }

    @DisplayName("프로토콜이 다르면 예외가 발생한다.")
    @Test
    void create_Fail2() {
        assertThatThrownBy(() -> new Http11Request(
                new Http11RequestLine("GET /path HTTP/2.0"),
                new Http11RequestHeaders(List.of("Host: localhost::8080")),
                Http11RequestBody.ofEmpty())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 HTTP 프로토콜 형식이 아닙니다.");
    }

    @DisplayName("헤더 형식이 부적합하면 예외가 발생한다.")
    @Test
    void create_Fail3() {
        assertThatThrownBy(() -> new Http11Request(
                new Http11RequestLine("GET /path HTTP/1.1"),
                new Http11RequestHeaders(List.of("Host:localhost::8080")),
                Http11RequestBody.ofEmpty())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 헤더 형식입니다.");
    }

    @DisplayName("존재하지 않는 HTTP Method로 생성하면 예외가 발생한다.")
    @Test
    void create_Fail4() {
        assertThatThrownBy(() -> new Http11Request(
                new Http11RequestLine("ASD /path HTTP/1.1"),
                new Http11RequestHeaders(List.of("Host:localhost::8080")),
                Http11RequestBody.ofEmpty())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 HTTP Method가 아닙니다.");
    }
}
