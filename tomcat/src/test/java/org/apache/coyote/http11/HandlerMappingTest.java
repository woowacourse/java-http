package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerMappingTest {

    @ParameterizedTest
    @MethodSource("existRequestAndExpect")
    @DisplayName("올바른 handlerMapping을 찾는다.")
    void find_correct_handler_mapping(
        final String path,
        final HttpMethod httpMethod,
        final HandlerMapping expect) {
        // given
        // when
        final HandlerMapping result = HandlerMapping.find(path, httpMethod);

        // then
        assertThat(result).isEqualTo(expect);
    }

    private static Stream<Arguments> existRequestAndExpect() {
        return Stream.of(
            Arguments.of("/", HttpMethod.GET, HandlerMapping.MAIN),
            Arguments.of("/index.html", HttpMethod.GET, HandlerMapping.INDEX)
        );
    }

    @ParameterizedTest
    @MethodSource("notExistRequestAndExpect")
    @DisplayName("존재하지 않는 handlerMapping을 찾으려고 하면 null를 반환한다.")
    void find_not_exist_handler_mapping(
        final String path,
        final HttpMethod httpMethod,
        final HandlerMapping expect) {
        // given
        // when
        final HandlerMapping result = HandlerMapping.find(path, httpMethod);

        // then
        assertThat(result).isEqualTo(expect);
    }

    private static Stream<Arguments> notExistRequestAndExpect() {
        return Stream.of(
            Arguments.of("/", HttpMethod.POST, null),
            Arguments.of("/user", HttpMethod.GET, null)
        );
    }
}
