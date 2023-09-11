package org.apache.coyote.http11;

import common.http.ControllerManager;
import nextstep.jwp.controller.HomeController;
import org.apache.catalina.startup.DynamicControllerManager;
import org.apache.coyote.Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final StubSocket socket = new StubSocket();
        final ControllerManager controllerManager = new DynamicControllerManager();
        controllerManager.add("/", new HomeController());
        final Processor processor = new Http11Processor(socket, controllerManager);

        // when
        processor.process(socket);

        // then
        var expected = List.of("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );

        assertThat(socket.output()).contains(expected);
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
        final var controllerManager = new DynamicControllerManager();
        final Http11Processor processor = new Http11Processor(socket, controllerManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = List.of("HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 5564 \r\n",
                "\r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).contains(expected);
    }
}
