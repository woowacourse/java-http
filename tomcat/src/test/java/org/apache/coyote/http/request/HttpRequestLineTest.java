package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @Test
    @DisplayName("정상적인 요청 라인 파싱")
    void parseNormalRequestLine() {
        // given
        final String rawRequestLine = "GET /path HTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(requestLine.getPath()).isEqualTo("/path");
            softly.assertThat(requestLine.getVersion()).isEqualTo("1.1");
        });
    }

    @Test
    @DisplayName("다중 공백이 포함된 요청 라인 파싱")
    void parseRequestLineWithMultipleSpaces() {
        // given
        final String rawRequestLine = "GET  /path   HTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(requestLine.getPath()).isEqualTo("/path");
            softly.assertThat(requestLine.getVersion()).isEqualTo("1.1");
        });
    }

    @Test
    @DisplayName("탭이 포함된 요청 라인 파싱")
    void parseRequestLineWithTabs() {
        // given
        final String rawRequestLine = "GET\t/path\tHTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(requestLine.getPath()).isEqualTo("/path");
            softly.assertThat(requestLine.getVersion()).isEqualTo("1.1");
        });
    }

    @Test
    @DisplayName("공백과 탭이 혼재된 요청 라인 파싱")
    void parseRequestLineWithMixedWhitespace() {
        // given
        final String rawRequestLine = "POST \t /api/login  \t HTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.POST);
            softly.assertThat(requestLine.getPath()).isEqualTo("/api/login");
            softly.assertThat(requestLine.getVersion()).isEqualTo("1.1");
        });
    }

    @Test
    @DisplayName("앞뒤 공백이 있는 요청 라인 파싱")
    void parseRequestLineWithLeadingTrailingSpaces() {
        // given
        final String rawRequestLine = "  GET /path HTTP/1.1  ";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(requestLine.getPath()).isEqualTo("/path");
            softly.assertThat(requestLine.getVersion()).isEqualTo("1.1");
        });
    }

    @Test
    @DisplayName("쿼리 파라미터가 포함된 요청 라인 파싱")
    void parseRequestLineWithQueryParams() {
        // given
        final String rawRequestLine = "GET /login?account=gugu&password=password HTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(requestLine.getPath()).isEqualTo("/login");
            softly.assertThat(requestLine.getVersion()).isEqualTo("1.1");
            softly.assertThat(requestLine.getQueryParam("account")).isEqualTo("gugu");
            softly.assertThat(requestLine.getQueryParam("password")).isEqualTo("password");
        });
    }

    @Test
    @DisplayName("URL 디코딩이 포함된 쿼리 파라미터")
    void parseQueryParamsWithUrlDecoding() {
        // given
        final String rawRequestLine = "GET /search?q=hello%20world&email=test%40example.com HTTP/1.1";

        // when
        final HttpRequestLine requestLine = HttpRequestLine.from(rawRequestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(requestLine.getPath()).isEqualTo("/search");
            softly.assertThat(requestLine.getVersion()).isEqualTo("1.1");
            softly.assertThat(requestLine.getQueryParam("q")).isEqualTo("hello world");
            softly.assertThat(requestLine.getQueryParam("email")).isEqualTo("test@example.com");
        });
    }

    @Test
    @DisplayName("잘못된 요청 라인 형식 - HTTP 버전 없음")
    void parseInvalidRequestLineWithoutHttpVersion() {
        // given
        final String rawRequestLine = "GET /path";

        // when & then
        assertThatThrownBy(() -> HttpRequestLine.from(rawRequestLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 요청의 첫 번째 줄은");
    }

    @Test
    @DisplayName("잘못된 요청 라인 형식 - 토큰 수 부족")
    void parseInvalidRequestLineWithInsufficientTokens() {
        // given
        final String rawRequestLine = "GET HTTP/1.1";

        // when & then
        assertThatThrownBy(() -> HttpRequestLine.from(rawRequestLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 요청의 첫 번째 줄은 3개의 부분으로 이뤄져야 합니다");
    }

    @Test
    @DisplayName("빈 요청 라인")
    void parseEmptyRequestLine() {
        // when & then
        assertThatThrownBy(() -> HttpRequestLine.from(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("null 요청 라인")
    void parseNullRequestLine() {
        // when & then
        assertThatThrownBy(() -> HttpRequestLine.from(null))
                .isInstanceOf(NullPointerException.class);
    }
}