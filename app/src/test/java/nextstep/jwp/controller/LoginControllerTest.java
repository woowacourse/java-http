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
                "\n"+
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
        String expectedBody = "login success!" + LINE_SEPARATOR +
        "account: gugu" + LINE_SEPARATOR +
        "email: hkkang@woowahan.com";

        assertThat(socket.output()).contains(expectedBody);
    }
}
