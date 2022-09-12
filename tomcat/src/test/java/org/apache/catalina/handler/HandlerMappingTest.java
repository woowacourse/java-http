package org.apache.catalina.handler;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import support.TestRequest;

class HandlerMappingTest {

    @ParameterizedTest
    @MethodSource("controllers")
    @DisplayName("Path에 맞는 Controller를 반환한다.")
    void getController(final String path, final Controller expected) {
        // given
        final HandlerMapping handlerMapping = HandlerMapping.getInstance();
        final HttpRequest request = TestRequest.generateWithUri(path);

        // when
        final Controller actual = handlerMapping.getController(request);

        // then
        assertThat(actual).isInstanceOf(expected.getClass());
    }

    private static Stream<Arguments> controllers() {
        return Stream.of(
            Arguments.of("/index", new DefaultController()),
            Arguments.of("/login", new LoginController()),
            Arguments.of("/register", new RegisterController())
        );
    }

    @Test
    @DisplayName("Path에 해당하는 Controller가 없을 경우 null을 반환한다.")
    void getNullWhenInvalidPath() {
        // given
        final HandlerMapping handlerMapping = HandlerMapping.getInstance();
        final String invalidPath = "";
        final HttpRequest request = TestRequest.generateWithUri(invalidPath);

        // when
        final Controller actual = handlerMapping.getController(request);

        // then
        assertThat(actual).isNull();
    }
}
