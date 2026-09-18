package org.apache.coyote.http11.request;

import org.apache.coyote.http11.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {
    @Test
    void parseRequestLine() {
        // when
        final RequestLine requestLine = RequestLine.from("GET /login?account=gugu HTTP/1.1");

        // then
        assertThat(requestLine.getMethod()).isEqualTo("GET");
        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getParams("account")).isEqualTo("gugu");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void invalidRequestLine() {
        assertThatThrownBy(() -> RequestLine.from("GET /index.html"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("잘못된 http요청 형태입니다.");
    }

    @Test
    void unsupportedVersion() {
        assertThatThrownBy(() -> RequestLine.from("GET /index.html HTTP/2"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 버전입니다: HTTP/2");
    }

    @Test
    void unsupportedMethod() {
        // given
        final RequestLine requestLine = RequestLine.from("POST /index.html HTTP/1.1");

        // when & then
        assertThatThrownBy(() -> requestLine.validateMethod(Set.of("GET")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 메서드입니다: POST");
    }
}
