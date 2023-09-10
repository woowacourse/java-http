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
class HomeControllerTest {

    private static final StubRequestMapper MAPPER = new StubRequestMapper();

    @Test
    void get_method_index_페이지_출력_테스트() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, MAPPER);

        byte[] html = "Hello world!".getBytes();

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html; charset=utf-8",
                "Content-Length: " + html.length,
                "",
                "Hello world!"
        );

        assertThat(socket.output()).isEqualTo(expected);
    }
}
