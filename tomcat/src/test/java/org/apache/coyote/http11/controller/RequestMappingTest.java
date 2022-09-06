package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.apache.coyote.http11.http.HttpRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import support.BufferedReaderFactory;

class RequestMappingTest {

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("/", HomeController.class),
                Arguments.of("/index.html", ResourceController.class),
                Arguments.of("/login", LoginController.class),
                Arguments.of("/login?account=gugu&password=password", LoginController.class),
                Arguments.of("/register", RegisterController.class),
                Arguments.of("/register", RegisterController.class)
        );
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void lookUp(final String uri, final Class<Controller> type) {
        String requestMessage = String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        HttpRequest httpRequest = HttpRequest.from(BufferedReaderFactory.getBufferedReader(requestMessage));
        RequestMapping requestMapping = new RequestMapping();
        Controller controller = requestMapping.getController(httpRequest);

        assertThat(controller).isInstanceOf(type);
    }
}
