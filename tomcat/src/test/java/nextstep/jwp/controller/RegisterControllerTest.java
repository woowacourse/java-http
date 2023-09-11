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
class RegisterControllerTest {

    private static final StubRequestMapper MAPPER = new StubRequestMapper();

    @Test
    void get_method_register_페이지_출력_테스트() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, MAPPER);

        // when
        processor.process(socket);
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
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
    void 아이디가_중복된_경우_401페이지_리다이렉트() {
        // given
        final String registerRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 58",
                "Connection: keep-alive",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        final var registerSocket = new StubSocket(registerRequest);
        final Http11Processor registerProcessor = new Http11Processor(registerSocket, MAPPER);

        // when
        registerProcessor.process(registerSocket);

        String registerOutput = registerSocket.output();

        String expected = String.join("\r\n", "HTTP/1.1 302 FOUND",
                "Location: /401.html",
                "",
                ""
        );

        assertThat(registerOutput).isEqualTo(expected);
    }

    @Test
    void 회원가입_성공_후_index_페이지로_리다이렉트() {
        // given
        final String registerRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 58",
                "Connection: keep-alive",
                "Accept: */*",
                "",
                "account=power&password=power&email=hkkang%40woowahan.com");

        final var registerSocket = new StubSocket(registerRequest);
        final Http11Processor registerProcessor = new Http11Processor(registerSocket, MAPPER);

        // when
        registerProcessor.process(registerSocket);
        String registerOutput = registerSocket.output();

        Assertions.assertAll(
                () -> assertThat(registerOutput).contains("Location: /index.html"),
                () -> assertThat(registerOutput).contains("Set-Cookie: JSESSIONID=")
        );
    }
}
