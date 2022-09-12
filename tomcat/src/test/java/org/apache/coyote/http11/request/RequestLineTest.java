package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestLineTest {

    @DisplayName("RequestLine을 파싱한다")
    @ParameterizedTest
    @MethodSource("provideForParse")
    void parse(final String requestLine, final RequestMethod method, final String path, final Params params) {
        final RequestLine actual = RequestLine.parse(requestLine);

        assertAll(
                () -> assertThat(actual.getMethod()).isEqualTo(method),
                () -> assertThat(actual.getPath()).isEqualTo(path),
                () -> assertThat(actual.getParams()).usingRecursiveComparison()
                        .isEqualTo(params)
        );
    }

    private static Stream<Arguments> provideForParse() {
        return Stream.of(
                Arguments.of("GET / HTTP/1.1", RequestMethod.GET, "/", Params.empty()),
                Arguments.of("GET /login HTTP/1.1", RequestMethod.GET, "/login", Params.empty()),
                Arguments.of("GET /login?a=a&b=123 HTTP/1.1", RequestMethod.GET, "/login", Params.parse("a=a&b=123"))
        );
    }
}