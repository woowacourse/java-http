package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    @DisplayName("유효한 request line은 method, uri, version으로 분리된다.")
    void parseValidRequestLine() {
        // given
        String line = "GET /login?account=gugu HTTP/1.1";

        // when
        RequestLine requestLine = new RequestLine(line);

        // then
        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getRequestUri().getPath()).isEqualTo("/login");
        assertThat(requestLine.getRequestUri().getQueryParams().getValue("account")).isEqualTo("gugu");
        assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    @DisplayName("POST 메서드와 HTTP/1.0 버전도 분리된다.")
    void parsePostAndHttp10() {
        // given
        String line = "POST /register HTTP/1.0";

        // when
        RequestLine requestLine = new RequestLine(line);

        // then
        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(requestLine.getRequestUri().getPath()).isEqualTo("/register");
        assertThat(requestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_0);
    }

    @Test
    @DisplayName("형식이 잘못된 request line은 예외가 발생한다.")
    void malformedRequestLineThrows() {
        // given
        List<String> malformedLines = List.of(
                "",                          // 빈 문자열
                "GET /login",                // version 누락
                "GET  /login HTTP/1.1",      // 구분 공백이 2칸
                "GET /login HTTP/1.1 extra", // 토큰 초과
                "get /login HTTP/1.1",       // 소문자 method
                "GET /login HTTP/1"          // 잘못된 version 형식
        );

        for (String line : malformedLines) {
            // when & then
            assertThatThrownBy(() -> new RequestLine(line))
                    .as(line)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 Request Line");
        }
    }

    @Test
    @DisplayName("정의되지 않은 method면 예외가 발생한다.")
    void undefinedMethodThrows() {
        // given
        String line = "FOO /login HTTP/1.1";

        // when & then
        assertThatThrownBy(() -> new RequestLine(line))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지원하지 않는 version이면 예외가 발생한다.")
    void unsupportedVersionThrows() {
        // given
        String line = "GET /login HTTP/9.9";

        // when & then
        assertThatThrownBy(() -> new RequestLine(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 HTTP 버전");
    }

}
