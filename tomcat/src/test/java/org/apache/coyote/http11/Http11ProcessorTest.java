package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.RequestHandler;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var tomcatController = new RequestHandler();
        final var httpRequestParser = new HttpRequestParser();
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, httpRequestParser, tomcatController);

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
        final var tomcatController = new RequestHandler();
        User user = InMemoryUserRepository.findByAccount("gugu").get();
        String sessionId = user.getSessionId();
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var httpRequestParser = new HttpRequestParser();
        final Http11Processor processor = new Http11Processor(socket, httpRequestParser, tomcatController);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
