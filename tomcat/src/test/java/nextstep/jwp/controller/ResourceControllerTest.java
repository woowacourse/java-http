package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubRequestMapper;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResourceControllerTest {

    private static final StubRequestMapper MAPPER = new StubRequestMapper();

    @Test
    void get_method_존재하지_않는_페이지_요청시_404_페이지_리다이렉트() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /hello HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, MAPPER);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n", "HTTP/1.1 302 FOUND",
                "Location: /404.html",
                "",
                ""
        );

        assertThat(socket.output()).isEqualTo(expected);
    }
}
