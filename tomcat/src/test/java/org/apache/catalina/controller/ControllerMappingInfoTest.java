package org.apache.catalina.controller;

import org.apache.coyote.request.HttpMethod;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ControllerMappingInfoTest {

    @ParameterizedTest
    @MethodSource("controllerMappingInfoDummy")
    void HttpMethod와_파라미터_유무와_요청_path로_생성에_성공한다(
            Expected expected
    ) {
        assertThatCode(() -> ControllerMappingInfo.of(expected.httpMethod, expected.isParameterized, expected.requestUri))
                .doesNotThrowAnyException();
    }

    private static class Expected {
        private final HttpMethod httpMethod;
        private final boolean isParameterized;
        private final String requestUri;

        public Expected(final HttpMethod httpMethod, final boolean isParameterized, final String requestUri) {
            this.httpMethod = httpMethod;
            this.isParameterized = isParameterized;
            this.requestUri = requestUri;
        }
    }

    private static Stream<Arguments> controllerMappingInfoDummy() {
        return Stream.of(
                Arguments.arguments(new Expected(HttpMethod.GET, false, "/index.html")),
                Arguments.arguments(new Expected(HttpMethod.GET, true, "/index.html?name=hyena")),
                Arguments.arguments(new Expected(HttpMethod.POST, false, "/index.html")),
                Arguments.arguments(new Expected(HttpMethod.POST, true, "/index.html?name=hyena"))
        );
    }
}
