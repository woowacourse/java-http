package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import nextstep.jwp.handler.Controller;
import nextstep.jwp.handler.DefaultController;
import nextstep.jwp.handler.ErrorController;
import nextstep.jwp.handler.FileController;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestMappingTest {

    @DisplayName(value = "url에 따라 올바른 Controller 검색")
    @ParameterizedTest
    @MethodSource("findParameters")
    void of(final String path, final Controller expected) {
        // given & when
        final Controller actual = RequestMapping.of(path);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> findParameters() {
        return Stream.of(
                Arguments.of("/", DefaultController.getInstance()),
                Arguments.of("/login", LoginController.getInstance()),
                Arguments.of("/register", RegisterController.getInstance()),
                Arguments.of("file", FileController.getInstance()),
                Arguments.of("error", ErrorController.getInstance())
        );
    }
}
