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
            Arguments.of("/index.html", HttpMethod.GET, HandlerMapping.INDEX),
            Arguments.of("/css/styles.css", HttpMethod.GET, HandlerMapping.INDEX_STYLE),
            Arguments.of("/js/scripts.js", HttpMethod.GET, HandlerMapping.INDEX_JS),
            Arguments.of("/assets/chart-area.js", HttpMethod.GET, HandlerMapping.CHART_AREA_JS),
            Arguments.of("/assets/chart-pie.js", HttpMethod.GET, HandlerMapping.CHART_PIE_JS),
            Arguments.of("/assets/chart-bar.js", HttpMethod.GET, HandlerMapping.CHART_BAR_JS),
            Arguments.of("/login", HttpMethod.GET, HandlerMapping.LOGIN),
            Arguments.of("/login", HttpMethod.POST, HandlerMapping.LOGIN_POST),
            Arguments.of("/401.html", HttpMethod.GET, HandlerMapping.UNAUTHORIZED),
            Arguments.of("/register", HttpMethod.GET, HandlerMapping.REGISTER),
            Arguments.of("/register", HttpMethod.POST, HandlerMapping.REGISTER_POST)
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
