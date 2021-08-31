package nextstep.jwp.controller;

import nextstep.jwp.MockSocket;
import nextstep.jwp.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundControllerTest {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    @DisplayName("유효하지 않은 URL에 대한 응답은 404html 페이지와 Not Found 응답 코드로 응답한다.")
    void notFoundTest() throws IOException {
        final String httpRequest = "GET /invalid-path HTTP/1.1";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        byte[] expectedBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        String expected = String.join(LINE_SEPARATOR,
                "HTTP/1.1 404 NOT_FOUND",
                        "Content-Length: " + expectedBody.length,
                        "Content-Type: text/html;charset=utf-8" + LINE_SEPARATOR,
                        new String(expectedBody));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
