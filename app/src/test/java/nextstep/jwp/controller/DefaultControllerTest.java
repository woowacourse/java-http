package nextstep.jwp.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultControllerTest extends ControllerTest {

    @DisplayName("GET css/styles.css 요청 시 style.css를 응답한다.")
    @Test
    void doGet() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        // when
        sendRequest(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css \r\n" +
                "Content-Length: 211991 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 resource 요청 시 404.html를 응답한다.")
    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /home.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        // when
        sendRequest(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 2426 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }
}
