package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.apache.coyote.http11.request.exception.HttpMethodNotAllowedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HttpMethodTest {

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("GET", HttpMethod.GET),
                Arguments.of("POST", HttpMethod.POST),
                Arguments.of("PATCH", HttpMethod.PATCH),
                Arguments.of("PUT", HttpMethod.PUT),
                Arguments.of("DELETE", HttpMethod.DELETE)
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    @DisplayName("Http 메소드 이름으로 HttpMethod를 찾는다.")
    void findHttpMethod(final String httpMethodName, final HttpMethod httpMethod) {
        // when
        HttpMethod findHttpMethod = HttpMethod.findHttpMethod(httpMethodName);

        // then
        assertThat(findHttpMethod).isEqualTo(httpMethod);
    }

    @Test
    @DisplayName("없는 Http 메소드 이름으로 HttpMethod를 찾으면 예외가 발생한다.")
    void findHttpMethod() {
        // given
        final String notExistHttpMethodName = "notExistMethodName";

        // when & then
        assertThatThrownBy(() -> HttpMethod.findHttpMethod(notExistHttpMethodName))
                .isInstanceOf(HttpMethodNotAllowedException.class)
                .hasMessage("허용되지 않는 HTTP Method입니다.");
    }
}
