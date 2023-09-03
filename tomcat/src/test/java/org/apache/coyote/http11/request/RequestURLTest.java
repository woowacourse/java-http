package org.apache.coyote.http11.request;

import org.apache.coyote.http11.exception.InvalidRequestLineException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RequestURLTest {

    static Stream<Arguments> firstLines() {
        return Stream.of(
                Arguments.of("GET /ocean?donghae=1 HTTP/1.1"),
                Arguments.of("GET /ocean HTTP/1.1"),
                Arguments.of("GET /ocean.html HTTP/1.1")
        );
    }

    @ParameterizedTest(name = "{index} {0} RequestUrl을 생성 성공 테스트")
    @MethodSource("firstLines")
    void from(final String firstLine) {
        // given

        // when & then
        assertDoesNotThrow(() -> RequestURL.from(firstLine));
    }

    @ParameterizedTest(name = "{index} {0} RequestUrl을 생성 실패 테스트")
    @EmptySource
    void from_exception(final String firstLine) {
        // given

        // when & then
        assertThatThrownBy(() -> RequestURL.from(firstLine))
                .isInstanceOf(InvalidRequestLineException.class)
                .hasMessage("잘못된 RequestURL입니다.");
    }

    @ParameterizedTest(name = "{index} {0} 절대 경로 조회 테스트.")
    @MethodSource("firstLines")
    void getAbsolutePath(final String firstLine) {
        // given
        final RequestURL requestURL = RequestURL.from(firstLine);

        // when
        final String absolutePath = requestURL.getAbsolutePath();

        // then
        assertThat(absolutePath).isEqualTo("/ocean.html");
    }

    @Test
    @DisplayName("확장자 조회 테스트")
    void getExtension() {
        // given
        final String firstLine = "GET /ocean.css HTTP/1.1";
        final RequestURL requestURL = RequestURL.from(firstLine);

        // when
        final String extension = requestURL.getExtension();

        // then
        assertThat(extension).isEqualTo("css");
    }
}
