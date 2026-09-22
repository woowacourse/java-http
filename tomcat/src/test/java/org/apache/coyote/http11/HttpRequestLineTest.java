package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP 요청 라인")
class HttpRequestLineTest {

    @Nested
    @DisplayName("올바른 요청 라인 파싱")
    class ValidRequestLineParsing {

        @Test
        @DisplayName("HTTP Method를 보존한다")
        void preservesMethod() {
            // given
            final var value = "POST /register HTTP/1.1";

            // when
            final var requestLine = HttpRequestLine.parse(value).orElseThrow();

            // then
            assertThat(requestLine.method()).isEqualTo("POST");
        }

        @Test
        @DisplayName("요청 URI를 보존한다")
        void preservesUri() {
            // given
            final var value = "POST /register HTTP/1.1";

            // when
            final var requestLine = HttpRequestLine.parse(value).orElseThrow();

            // then
            assertThat(requestLine.uri()).isEqualTo(URI.create("/register"));
        }

        @Test
        @DisplayName("HTTP 버전을 보존한다")
        void preservesVersion() {
            // given
            final var value = "POST /register HTTP/1.1";

            // when
            final var requestLine = HttpRequestLine.parse(value).orElseThrow();

            // then
            assertThat(requestLine.version()).isEqualTo("HTTP/1.1");
        }
    }

    @ParameterizedTest(name = "{displayName} | 입력: {0}")
    @ValueSource(strings = {
            "POST /register",
            "POST /register HTTP/1.1 EXTRA"
    })
    @DisplayName("항목이 세 개가 아니면 파싱하지 않는다")
    void rejectsUnexpectedPartCount(final String value) {
        // given: 요청 라인은 @ValueSource에서 전달된다.

        // when
        final var requestLine = HttpRequestLine.parse(value);

        // then
        assertThat(requestLine).isEmpty();
    }

    @Test
    @DisplayName("요청 대상이 URI 형식이 아니면 파싱하지 않는다")
    void rejectsInvalidUri() {
        // given
        final var value = "GET /login?account=%ZZ HTTP/1.1";

        // when
        final var requestLine = HttpRequestLine.parse(value);

        // then
        assertThat(requestLine).isEmpty();
    }
}
