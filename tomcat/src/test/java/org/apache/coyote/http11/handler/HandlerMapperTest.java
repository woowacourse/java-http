package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerMapperTest {

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("/", HomeHandler.class),
                Arguments.of("/index.html", ResourceHandler.class),
                Arguments.of("/login", LoginHandler.class)
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void lookUp(final String uri, final Class<Handler> type) {
        Handler handler = HandlerMapper.lookUp(uri);

        assertThat(handler).isInstanceOf(type);
    }
}
