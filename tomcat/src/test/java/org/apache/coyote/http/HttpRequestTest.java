package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("정상적인 요청 라인 파싱")
    void parseNormalRequestLine() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /path HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/path");
        assertThat(request.getVersion()).isEqualTo("1.1");
    }

    @Test
    @DisplayName("다중 공백이 포함된 요청 라인 파싱")
    void parseRequestLineWithMultipleSpaces() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET  /path   HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/path");
        assertThat(request.getVersion()).isEqualTo("1.1");
    }

    @Test
    @DisplayName("탭이 포함된 요청 라인 파싱")
    void parseRequestLineWithTabs() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET\t/path\tHTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/path");
        assertThat(request.getVersion()).isEqualTo("1.1");
    }

    @Test
    @DisplayName("공백과 탭이 혼재된 요청 라인 파싱")
    void parseRequestLineWithMixedWhitespace() {
        // given
        final String rawRequest = String.join("\r\n",
                "POST \t /api/login  \t HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/json",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/api/login");
        assertThat(request.getVersion()).isEqualTo("1.1");
    }

    @Test
    @DisplayName("앞뒤 공백이 있는 요청 라인 파싱")
    void parseRequestLineWithLeadingTrailingSpaces() {
        // given
        final String rawRequest = String.join("\r\n",
                "  GET /path HTTP/1.1  ",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/path");
        assertThat(request.getVersion()).isEqualTo("1.1");
    }

    @Test
    @DisplayName("쿼리 파라미터가 포함된 요청 라인 파싱")
    void parseRequestLineWithQueryParams() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET  /login?account=gugu&password=password   HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getVersion()).isEqualTo("1.1");
        assertThat(request.getQueryParam("account")).isEqualTo("gugu");
        assertThat(request.getQueryParam("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("잘못된 요청 라인 형식 - HTTP 버전 없음")
    void parseInvalidRequestLineWithoutHttpVersion() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /path",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNull();
    }

    @Test
    @DisplayName("잘못된 요청 라인 형식 - 토큰 수 부족")
    void parseInvalidRequestLineWithInsufficientTokens() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(rawRequest);

        // then
        assertThat(request).isNull();
    }

    @Test
    @DisplayName("빈 요청 문자열")
    void parseEmptyRequest() {
        // when
        final HttpRequest request = HttpRequest.from("");

        // then
        assertThat(request).isNull();
    }

    @Test
    @DisplayName("null 요청 문자열")
    void parseNullRequest() {
        // when
        final HttpRequest request = HttpRequest.from(null);

        // then
        assertThat(request).isNull();
    }
}