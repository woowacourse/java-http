package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class HandlerAdapterTest {

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("/", HomeHandler.class),
                Arguments.of("/index.html", ResourceHandler.class),
                Arguments.of("/login", LoginHandler.class),
                Arguments.of("/login?account=gugu&password=password", LoginHandler.class)
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    @DisplayName("RequestUri를 처리할 수 있는 Handler를 반환한다.")
    void findRequestHandler(final String requestUri, final Class<RequestHandler> requestHandlerClass) {
        // when
        RequestHandler findRequestHandler = HandlerAdapter.findRequestHandler(requestUri);

        // then
        assertThat(findRequestHandler).isInstanceOf(requestHandlerClass);
    }
}
