package org.apache.coyote.http11;

import com.techcourse.controller.RootController;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.StaticResourceController;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session-id",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new RequestMapping(
                new StaticResourceController(), List.of(new RootController()));
        final var processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session-id",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new RequestMapping(new StaticResourceController(), List.of());
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] resourceBytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + resourceBytes.length + "\r\n" +
                "\r\n"+
                new String(resourceBytes, StandardCharsets.UTF_8);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
