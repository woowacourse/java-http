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

class StaticResourceControllerTest {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    @DisplayName("요청 URL로부터 정적 파일에 대한 요청인지 구분하여 정상 처리할 수 있다.")
    void requestStaticFile() throws IOException {
        // given
        final String httpRequest = String.join(LINE_SEPARATOR,
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] expectedBody = Files.readAllBytes(new File(resource.getFile()).toPath());
        String expected = "HTTP/1.1 200 OK\n" +
                "Content-Length: " + expectedBody.length + "\n" +
                "Content-Type: text/html;charset=utf-8\n" +
                "\n" +
                new String(expectedBody);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
