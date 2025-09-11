package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request).isNotNull();
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/path");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
            softly.assertThat(request.getHeader("host")).isEqualTo("localhost:8080");
        });
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
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request).isNotNull();
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/path");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
        });
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
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request).isNotNull();
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/path");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
        });
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
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request).isNotNull();
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
            softly.assertThat(request.getPath()).isEqualTo("/api/login");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
        });
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
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request).isNotNull();
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/path");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
        });
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
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request).isNotNull();
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/login");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
            softly.assertThat(request.getQueryParam("account")).isEqualTo("gugu");
            softly.assertThat(request.getQueryParam("password")).isEqualTo("password");
        });
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

        // when & then
        assertThatThrownBy(() -> parseRequest(rawRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 요청의 첫 번째 줄은");
    }

    @Test
    @DisplayName("잘못된 요청 라인 형식 - 토큰 수 부족")
    void parseInvalidRequestLineWithInsufficientTokens() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when & then
        assertThatThrownBy(() -> parseRequest(rawRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 요청의 첫 번째 줄은 3개의 부분으로 이뤄져야 합니다");
    }

    @Test
    @DisplayName("빈 요청 문자열")
    void parseEmptyRequest() {
        // when & then
        assertThatThrownBy(() -> parseRequest(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HTTP 헤더는 null이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("null 요청 문자열")
    void parseNullRequest() {
        // when & then
        assertThatThrownBy(() -> parseRequest(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("POST 요청 본문 파싱")
    void parsePostRequestBody() {
        // given
        final String rawRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 52",
                "",
                "account=user&password=1234&email=user%40example.com");

        // when
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
            softly.assertThat(request.getBodyParam("account")).isEqualTo("user");
            softly.assertThat(request.getBodyParam("password")).isEqualTo("1234");
            softly.assertThat(request.getBodyParam("email")).isEqualTo("user@example.com");
        });
    }

    @Test
    @DisplayName("쿠키 파싱")
    void parseCookies() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=ABC123; theme=dark; lang=ko",
                "",
                "");

        // when
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request.getCookie("JSESSIONID")).isEqualTo("ABC123");
            softly.assertThat(request.getCookie("theme")).isEqualTo("dark");
            softly.assertThat(request.getCookie("lang")).isEqualTo("ko");
        });
    }

    @Test
    @DisplayName("쿠키가 없는 요청")
    void parseRequestWithoutCookies() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request.getCookie("JSESSIONID")).isEmpty();
            softly.assertThat(request.getCookie("nonexistent")).isEmpty();
        });
    }

    @Test
    @DisplayName("URL 디코딩이 포함된 쿼리 파라미터")
    void parseQueryParamsWithUrlDecoding() {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /search?q=hello%20world&email=test%40example.com HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = parseRequest(rawRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request.getQueryParam("q")).isEqualTo("hello world");
            softly.assertThat(request.getQueryParam("email")).isEqualTo("test@example.com");
        });
    }

    private HttpRequest parseRequest(final String rawRequest) {
        final String[] parts = rawRequest.split("\r\n\r\n", 2);
        final String headerPart = parts[0];
        final String bodyPart = parts.length > 1 ? parts[1] : "";

        final HttpRequestHeader header = HttpRequestHeader.from(headerPart);
        final HttpRequestBody body = HttpRequestBody.from(bodyPart, header.getContentType());

        return HttpRequest.from(header, body);
    }
}
