package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP 헤더")
class HttpHeaderTest {

    @Nested
    @DisplayName("올바른 헤더 파싱")
    class ValidHeaderParsing {

        @Test
        @DisplayName("콜론 앞의 헤더 이름을 보존한다")
        void preservesName() {
            // given
            final var line = "Host: localhost:8080";

            // when
            final var header = HttpHeader.parse(line).orElseThrow();

            // then
            assertThat(header.name()).isEqualTo("Host");
        }

        @Test
        @DisplayName("첫 번째 콜론 뒤의 값을 모두 보존한다")
        void preservesValueContainingColon() {
            // given
            final var line = "Host: localhost:8080";

            // when
            final var header = HttpHeader.parse(line).orElseThrow();

            // then
            assertThat(header.value()).isEqualTo("localhost:8080");
        }

        @Test
        @DisplayName("콜론 뒤에 공백이 없어도 값을 읽는다")
        void readsValueWithoutSpaceAfterColon() {
            // given
            final var line = "Content-Length:80";

            // when
            final var header = HttpHeader.parse(line).orElseThrow();

            // then
            assertThat(header.value()).isEqualTo("80");
        }
    }

    @Test
    @DisplayName("콜론이 없으면 파싱하지 않는다")
    void rejectsLineWithoutColon() {
        // given
        final var line = "Content-Length 80";

        // when
        final var header = HttpHeader.parse(line);

        // then
        assertThat(header).isEmpty();
    }

    @Test
    @DisplayName("헤더 이름이 없으면 파싱하지 않는다")
    void rejectsLineWithoutName() {
        // given
        final var line = ": value";

        // when
        final var header = HttpHeader.parse(line);

        // then
        assertThat(header).isEmpty();
    }
}
