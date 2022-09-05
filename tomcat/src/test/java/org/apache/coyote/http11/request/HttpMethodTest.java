package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {

    @ParameterizedTest
    @MethodSource("provideInputMethodAndHttpMethod")
    void http_method를_찾는다(String inputHttpMethod, HttpMethod expected) {
        // given
        HttpMethod httpMethod = HttpMethod.findHttpMethod(inputHttpMethod);

        // when & then
        assertThat(httpMethod).isEqualTo(expected);
    }

    public static Stream<Arguments> provideInputMethodAndHttpMethod() {
        return Stream.of(
                Arguments.of("GET", HttpMethod.GET),
                Arguments.of("POST", HttpMethod.POST)
        );
    }

}
