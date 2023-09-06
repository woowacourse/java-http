package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.context.HelloWorldContext;
import nextstep.handler.WelcomeHandler;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Http11ProcessorTest {

    @Test
    void process() {
        final var socket = new StubSocket();
        final HelloWorldContext context = new HelloWorldContext("/");
        context.addHandler(new WelcomeHandler());
        final var processor = new Http11Processor(socket, List.of(context));

        processor.process(socket);

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello World!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 존재하지_않는_contextPath로_요청하면_404가_발생한다() {
        final String httpRequest= String.join("\r\n",
                "GET /hello/index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, List.of(new HelloWorldContext("/")));

        processor.process(socket);

        final String actual = socket.output();

        assertThat(actual).contains("/404.html");
    }

    @Test
    void index() throws IOException {
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, List.of(new HelloWorldContext("/")));

        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
