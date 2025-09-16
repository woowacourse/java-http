package org.apache.coyote.http11;

import com.techcourse.util.StaticResourceManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @BeforeEach
    void setUp() {
        StaticResourceManager.initialize();
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expected = new ArrayList<>();
        expected.add("HTTP/1.1 200 OK \r\n");
        expected.add("Content-Type: text/html; charset=utf-8 \r\n");
        expected.add("Content-Length: 12 \r\n");
        expected.add("Hello world!");

        var actual = socket.output();

        assertThat(actual).contains(expected);
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

        List<String> expected = new ArrayList<>();
        expected.add("HTTP/1.1 200 OK \r\n");
        expected.add("Content-Type: text/html; charset=utf-8 \r\n");
        expected.add("Content-Length: 5564 \r\n");
        expected.add(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        var actual = socket.output();

        assertThat(actual).contains(expected);
    }
}
