package org.apache.coyote.http11;

import org.apache.coyote.Controller;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final Controller controller = (req, res) -> {
            res.setStatus(HttpStatus.OK);
            res.setBody(ResponseBody.from("Hello world!"));
        };
        final var processor = new Http11Processor(socket, controller);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
