package org.apache.coyote.http11.request;

import org.apache.coyote.http11.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {
    @Test
    void parseRequestLine() {
        // given
        final String requestLine = "GET /login?account=gugu HTTP/1.1";

        // when
        final HttpRequest request = HttpRequest.from(requestLine);

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getHttpPath()).isEqualTo("/login");
        assertThat(request.getParams("account")).isEqualTo("gugu");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void invalidRequestLine() {
        // given
        final String requestLine = "GET /index.html ABC HTTP/1.1";

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(requestLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 http요청 형태입니다.");
    }

    @Test
    void supportedMethod() {
        // given
        final String requestLine = "GET /index.html HTTP/1.1";
        final Set<String> supportedMethods = Set.of("GET");

        // when
        final HttpRequest request = HttpRequest.from(requestLine, supportedMethods);

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getHttpPath()).isEqualTo("/index.html");
    }

    @Test
    void unsupportedMethod() {
        // given
        final String requestLine = "POST /index.html HTTP/1.1";
        final Set<String> supportedMethods = Set.of("GET");

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(requestLine, supportedMethods))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 메서드입니다: POST");
    }

    @Test
    void withoutSupportedMethods() {
        // given
        final String requestLine = "POST /index.html HTTP/1.1";

        // when
        final HttpRequest request = HttpRequest.from(requestLine);

        // then
        assertThat(request.getMethod()).isEqualTo("POST");
    }

    @Test
    void unsupportedVersion() {
        // given
        final String requestLine = "GET /index.html ABC";

        // when & then
        assertThatThrownBy(() -> HttpRequest.from(requestLine))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("지원하지 않는 HTTP 버전입니다: ABC");
    }
}
