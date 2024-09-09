package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {

    @ParameterizedTest
    @DisplayName("String으로 HttpMethod enum을 찾는다.")
    @MethodSource("findParameterProvider")
    void find(final String target, final HttpMethod expected) {
        assertThat(HttpMethod.fromName(target)).isEqualTo(expected);
    }

    static Stream<Arguments> findParameterProvider() {
        return Stream.of(
                Arguments.of("GET", HttpMethod.GET),
                Arguments.of("POST", HttpMethod.POST),
                Arguments.of("DELETE", HttpMethod.DELETE),
                Arguments.of("PUT", HttpMethod.PUT)
        );
    }
}
