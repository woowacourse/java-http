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
            HttpMethod httpMethod,
            boolean isParameterized,
            String requestPath
    ) {
        assertThatCode(() -> ControllerMappingInfo.of(httpMethod, isParameterized, requestPath))
                .doesNotThrowAnyException();
    }

    static Stream<Arguments> controllerMappingInfoDummy() {
        return Stream.of(
                Arguments.arguments(HttpMethod.GET, false, "/index.html"),
                Arguments.arguments(HttpMethod.GET, true, "/index.html?name=hyena"),
                Arguments.arguments(HttpMethod.POST, false, "/index.html"),
                Arguments.arguments(HttpMethod.POST, true, "/index.html?name=hyena")
        );
    }
}
