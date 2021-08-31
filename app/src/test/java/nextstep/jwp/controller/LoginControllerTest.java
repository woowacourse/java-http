package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    @DisplayName("로그인 페이지를 응답한다.")
    void loginPage() throws IOException {
        // given
        final String httpRequest = "GET /login HTTP/1.1";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        byte[] expectedBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        String expected = String.join(LINE_SEPARATOR,
                "HTTP/1.1 200 OK",
                "Content-Length: " + expectedBody.length,
                "Content-Type: text/html;charset=utf-8" + LINE_SEPARATOR,
                new String(expectedBody));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 URL에 QueryParameter가 있는 경우 로그인 요청으로 처리한다.")
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
