package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import common.HttpMethod;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpRequestLineTest {

    static Stream<Arguments> validRequestLines() {
        return Stream.of(
                Arguments.of("GET /path HTTP/1.1", HttpMethod.GET, "/path", "1.1"),
                Arguments.of("GET  /path   HTTP/1.1", HttpMethod.GET, "/path", "1.1"),
                Arguments.of("GET\t/path\tHTTP/1.1", HttpMethod.GET, "/path", "1.1"),
                Arguments.of("POST \t /api/login  \t HTTP/1.1", HttpMethod.POST, "/api/login", "1.1"),
                Arguments.of("  GET /path HTTP/1.1  ", HttpMethod.GET, "/path", "1.1")
        );
    }

    static Stream<Arguments> invalidRequestLines() {
        return Stream.of(
                Arguments.of("GET /path", IllegalArgumentException.class, "HTTP 요청의 첫 번째 줄은"),
                Arguments.of("GET HTTP/1.1", IllegalArgumentException.class, "HTTP 요청의 첫 번째 줄은 3개의 부분으로 이뤄져야 합니다"),
                Arguments.of("", IllegalArgumentException.class, null),
                Arguments.of(null, NullPointerException.class, null)
        );
    }

    @ParameterizedTest(name = "요청 라인 파싱: {0}")
    @MethodSource("validRequestLines")
    @DisplayName("여러 형태의 요청 라인 파싱")
    void parseRequestLineVariants(String raw, HttpMethod method, String path, String version) {
        final HttpRequestLine requestLine = HttpRequestLine.from(raw);
        assertSoftly(softly -> {
            softly.assertThat(requestLine.getMethod()).isEqualTo(method);
            softly.assertThat(requestLine.getPath()).isEqualTo(path);
            softly.assertThat(requestLine.getVersion()).isEqualTo(version);
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

    @ParameterizedTest(name = "잘못된 요청 라인: {0}")
    @MethodSource("invalidRequestLines")
    @DisplayName("잘못된 요청 라인 예외")
    void parseInvalidRequestLine(String raw, Class<? extends Throwable> expected, String contains) {
        assertThatThrownBy(() -> HttpRequestLine.from(raw))
                .isInstanceOf(expected)
                .satisfies(e -> {
                    if (contains != null) {
                        assertSoftly(softly -> softly.assertThat(e.getMessage()).contains(contains));
                    }
                });
    }
}
