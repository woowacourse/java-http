package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ControllerMapperTest {

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("/", HomeController.class),
                Arguments.of("/index.html", ResourceController.class),
                Arguments.of("/login", LoginController.class)
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void lookUp(final String uri, final Class<Controller> type) {
        Controller controller = HandlerMapper.lookUp(uri);

        assertThat(controller).isInstanceOf(type);
    }
}
