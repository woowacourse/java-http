package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.ui.DashboardController;
import org.apache.config.CompositionRoot;
import org.apache.mvc.Controller;
import org.apache.mvc.HandlerMapper;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private final Controller controller = new DashboardController(new InMemoryUserRepository());
    private final HandlerMapper handlerMapper =
            new HandlerMapper(new CompositionRoot().getHandlerChain(List.of(controller)));

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, handlerMapper);

        // when
        processor.process(socket);
        String output = socket.output();

        // then
        var expected = List.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(output).contains(expected);
//        assertThat(output).contains("Content-Length: 12");
//        assertThat(output).contains("Content-Type: text/html;charset=utf-8");
//        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, handlerMapper);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = List.of("HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 5564",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).contains(expected);
    }
}
