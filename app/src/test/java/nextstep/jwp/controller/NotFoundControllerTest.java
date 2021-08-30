package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NotFoundControllerTest {
    @Test
    void notFoundTest() throws IOException {
        final String httpRequest = "GET /invalid-path HTTP/1.1";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expected =
                "HTTP/1.1 404 NOT_FOUND\n" +
                "Content-Length: 2426\n" +
                "Content-Type: text/html;charset=utf-8\n" +
                "\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
