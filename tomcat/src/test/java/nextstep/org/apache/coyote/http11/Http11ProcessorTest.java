package nextstep.org.apache.coyote.http11;

import nextstep.jwp.controller.HomeController;
import org.apache.coyote.controller.ControllerMapper;
import org.apache.coyote.http11.Http11Processor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        ControllerMapper.register("/", new HomeController());
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String actual = socket.output();
        assertAll(() -> {
            assertThat(actual).contains("HTTP/1.1 200 OK");
            assertThat(actual).contains("Content-Type: text/html;charset=utf-8");
        });
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));


        final String actual = socket.output();
        assertAll(() -> {
            assertThat(actual).contains("HTTP/1.1 200 OK");
            assertThat(actual).contains("Content-Type: text/html;charset=utf-8");
        });
    }
}
