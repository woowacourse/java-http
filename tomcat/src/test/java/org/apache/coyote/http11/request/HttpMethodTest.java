package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {

    static Stream<Arguments> httpMethodWithMatchedNameProvider() {
        return Stream.of(
                Arguments.of(
                        HttpMethod.GET,
                        "GET"
                ),
                Arguments.of(
                        HttpMethod.POST,
                        "POST"
                )
        );
    }

    @DisplayName("HttpMethod 이름으로 HttpMethod를 찾을 수 있다.")
    @ParameterizedTest
    @MethodSource("httpMethodWithMatchedNameProvider")
    void findByName(HttpMethod method, String name) {
        assertThat(HttpMethod.findByName(name)).isEqualTo(method);
    }
}
