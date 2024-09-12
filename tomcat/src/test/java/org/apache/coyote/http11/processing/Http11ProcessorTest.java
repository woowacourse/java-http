package org.apache.coyote.http11.processing;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.framework.FrameworkDispatcher;
import com.techcourse.framework.RequestMapping;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        final var socket = new StubSocket();
        final var requestMapping = new RequestMapping();
        final var dispatcher = new FrameworkDispatcher(requestMapping);
        final var processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        var expected = "HTTP/1.1 302 Found\r\n"
                + "Location: /index.html\r\n"
                + "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var requestMapping = new RequestMapping();
        final var dispatcher = new FrameworkDispatcher(requestMapping);
        final Http11Processor processor = new Http11Processor(socket, dispatcher);
        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
