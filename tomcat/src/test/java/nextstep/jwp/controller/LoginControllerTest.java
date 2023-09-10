package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubRequestMapper;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoginControllerTest {

    private static final StubRequestMapper MAPPER = new StubRequestMapper();

    @Test
    void get_method_login_페이지_출력_테스트() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, MAPPER);

        // when
        processor.process(socket);
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        byte[] body = Files.readAllBytes(Paths.get(resource.getFile()));

        // then
        String expected = String.join("\r\n", "HTTP/1.1 200 OK",
                "Content-Type: text/html; charset=utf-8",
                "Content-Length: " + body.length,
                "",
                new String(body)
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void post_method_login_페이지_출력_테스트() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 30",
                "Connection: keep-alive",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, MAPPER);

        // when
        processor.process(socket);
        String output = socket.output();
        Assertions.assertAll(
                () -> assertThat(output).contains("Location: /index.html"),
                () -> assertThat(output).contains("Set-Cookie: JSESSIONID=")
        );
    }

    @Test
    void 로그인한_사용자가_get_method_login_페이지_요청시_index_페이지로_리다이렉트() {
        // given
        final String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 30",
                "Connection: keep-alive",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var loginSocket = new StubSocket(loginRequest);
        final Http11Processor loginProcessor = new Http11Processor(loginSocket, MAPPER);

        // when
        loginProcessor.process(loginSocket);

        String loginOutput = loginSocket.output();
        String[] lines = loginOutput.split("\r\n");
        String sessionId = "";
        for (String line : lines) {
            if (line.contains("JSESSIONID")) {
                sessionId = line.split("=")[1];
                break;
            }
        }

        // given
        final String loginPageRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");

        final var loginPageSocket = new StubSocket(loginPageRequest);
        final Http11Processor loginPageProcessor = new Http11Processor(loginPageSocket, MAPPER);

        // when
        loginPageProcessor.process(loginPageSocket);
        String loginPageOutput = loginSocket.output();

        Assertions.assertAll(
                () -> assertThat(loginPageOutput).contains("Location: /index.html")
        );
    }
}
