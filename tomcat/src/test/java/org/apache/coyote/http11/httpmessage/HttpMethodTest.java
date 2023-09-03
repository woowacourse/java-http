package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMethodTest {

    @ParameterizedTest
    @MethodSource("inputAndHttpMethod")
    @DisplayName("올바른 http요청이 들어오면 에러를 발생하지 않는다.")
    void find_HTTP_method(final String input, final HttpMethod expect) {
        // given
        // when
        final HttpMethod result = HttpMethod.getMethod(input);

        // then
        assertThat(result).isEqualTo(expect);
    }

    private static Stream<Arguments> inputAndHttpMethod() {
        return Stream.of(
            Arguments.of("POST", HttpMethod.POST),
            Arguments.of("GET", HttpMethod.GET),
            Arguments.of("PUT", HttpMethod.PUT),
            Arguments.of("DELETE", HttpMethod.DELETE)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "post1", "get"})
    @DisplayName("올바르지 않는 http요청이 들어오면 에러를 발생한다.")
    void find_inCorrect_HTTP_method(final String input) {
        // given
        // when
        // then
        assertThatThrownBy(() -> HttpMethod.getMethod(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 httpMethod 입니다.");
    }
}
