package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest = "GET /login HTTP/1.1";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK\n" +
                "Content-Length: 3796\n" +
                "Content-Type: text/html;charset=utf-8\n" +
                "\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginProcess() {
        // given
        final String httpRequest = "GET /login?account=gugu&password=password HTTP/1.1";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        List<String> expectedLines = List.of("HTTP/1.1 302 FOUND", "Location: /index.html");


        assertThat(socket.output()).contains(expectedLines);
    }
}
