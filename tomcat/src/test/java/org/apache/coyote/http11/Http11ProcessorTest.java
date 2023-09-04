package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Http11ProcessorTest {

    @Test
    void 기본_페이지를_조회할_수_있다() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        List<String> lines = Arrays.stream(output.split("\r\n"))
                .collect(Collectors.toList());

        assertThat(lines).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"index.html", "login.html", "register.html"})
    void HTML_파일을_조회할_수_있다(String target) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /" + target + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/" + target);

        String output = socket.output();

        List<String> lines = Arrays.stream(output.split("\r\n"))
                .collect(Collectors.toList());

        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(lines).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body
        );
    }

    @Test
    void CSS_파일을_조회할_수_있다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        List<String> lines = Arrays.stream(output.split("\r\n"))
                .collect(Collectors.toList());

        assertThat(lines.get(0)).isEqualTo("HTTP/1.1 200 OK ");
    }

    @Test
    void 로그인_성공_시_인덱스_페이지로_이동한다() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        List<String> lines = Arrays.stream(output.split("\r\n"))
                .collect(Collectors.toList());

        assertThat(lines).contains(
                "HTTP/1.1 302 FOUND ",
                "Location: index.html "
        );
    }

    @Test
    void 로그인_실패_시_401_페이지로_이동한다() {
        // given
        final String requestBody = "account=gugu&password=wrongpassword";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        List<String> lines = Arrays.stream(output.split("\r\n"))
                .collect(Collectors.toList());

        assertThat(lines).contains(
                "HTTP/1.1 302 FOUND ",
                "Location: 401.html "
        );
    }

    @Test
    void 이미_로그인이_되었다면_로그인_페이지로_이동할_수_없다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=123-456",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        List<String> lines = Arrays.stream(output.split("\r\n"))
                .collect(Collectors.toList());

        assertThat(lines).contains(
                "HTTP/1.1 302 FOUND ",
                "Location: index.html "
        );
    }

    @Test
    void 회원가입_성공_시_인덱스_페이지로_이동한다() throws IOException {
        // given
        final String requestBody = "account=teo&password=123";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        String output = socket.output();

        List<String> lines = Arrays.stream(output.split("\r\n"))
                .collect(Collectors.toList());

        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(lines).contains(
                "HTTP/1.1 302 FOUND ",
                "Location: index.html ");
    }
}
