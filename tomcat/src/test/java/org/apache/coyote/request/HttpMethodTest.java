package org.apache.coyote.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.apache.coyote.request.HttpMethod.DELETE;
import static org.apache.coyote.request.HttpMethod.GET;
import static org.apache.coyote.request.HttpMethod.OPTION;
import static org.apache.coyote.request.HttpMethod.PATCH;
import static org.apache.coyote.request.HttpMethod.POST;
import static org.apache.coyote.request.HttpMethod.PUT;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {

    @ParameterizedTest
    @MethodSource("httpMethodDummy")
    void HttpMethod를_문자열로_조회할_수_있다(
            final String value,
            final HttpMethod expected
    ) {
        assertThat(HttpMethod.from(value)).isEqualTo(expected);
    }

    static Stream<Arguments> httpMethodDummy() {
        return Stream.of(
                Arguments.arguments("GET", GET),
                Arguments.arguments("POST", POST),
                Arguments.arguments("PATCH", PATCH),
                Arguments.arguments("PUT", PUT),
                Arguments.arguments("DELETE", DELETE),
                Arguments.arguments("OPTION", OPTION)
        );
    }
}
