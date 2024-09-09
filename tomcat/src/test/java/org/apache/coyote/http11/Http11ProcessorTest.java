package org.apache.coyote.http11;

import com.techcourse.executor.LoginController;
import com.techcourse.executor.RegisterController;
import org.apache.coyote.http11.executor.RequestExecutors;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    RequestExecutors requestExecutors = new RequestExecutors(
            Map.of("/login", new LoginController(),
                    "/register", new RegisterController()));
    SessionManager sessionManager = new SessionManager(150000);

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, requestExecutors, sessionManager);

        // when
        processor.process(socket);


        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 13 ",
                "",
                "Hello world!"
        );
    }

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
        final Http11Processor processor = new Http11Processor(socket, requestExecutors, sessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader()
                .getResource("static/index.html");

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }
}
