package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.http11.handler.DispatcherHandler;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

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
//                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: 12 ",
//                "",
//                "Hello world!");
//
//        assertThat(socket.output()).isEqualTo(expected);
//    }

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
        List<Handler> handlers = List.of(
                new StaticResourceHandler("static", "index.html"),
                new LoginHandler()
        );
        final Http11Processor processor = new Http11Processor(socket, new DispatcherHandler(handlers));

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
