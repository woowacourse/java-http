package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

//    @Test
//    void process() {
//        // given
//        final var socket = new StubSocket();
//        final var processor = new Http11Processor(socket);
//
//        // when
//        processor.process(socket);
//
//        // then
//        var expected = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Length: 12 ",
//                "Content-Type: text/html;charset=utf-8 ",
//                "",
//                "Hello world!");
//
//        assertThat(socket.output()).isEqualTo(expected);
//    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final String actualWithoutSetCookie = Arrays.stream(socket.output().split("\r\n"))
                .filter(message -> !message.contains("Set-Cookie"))
                .collect(Collectors.joining("\r\n"));
        assertThat(actualWithoutSetCookie).isEqualTo(expected);
    }
}
