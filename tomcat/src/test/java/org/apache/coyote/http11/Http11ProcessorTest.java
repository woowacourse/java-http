package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인에_성공하면_세션_쿠키를_발급하고_인덱스_페이지로_리다이렉트한다() {
        // given
        final var requestBody = "account=gugu&password=password";
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html\r\n")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36}\\r\\n");
    }

    @Test
    void 로그인에_실패하면_인증_실패_페이지로_리다이렉트한다() {
        // given
        final var requestBody = "account=gugu&password=wrong";
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html",
                "\r\n");

        assertThat(socket.output()).isEqualTo(expected);
        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    @Test
    void 로그인된_상태에서_로그인_페이지에_접근하면_인덱스_페이지로_리다이렉트한다() {
        // given
        final var requestBody = "account=gugu&password=password";
        final var loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        final var loginSocket = new StubSocket(loginRequest);
        new Http11Processor(loginSocket).process(loginSocket);

        final var sessionId = loginSocket.output()
                .lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();

        final var request = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");
        final var socket = new StubSocket(request);

        // when
        new Http11Processor(socket).process(socket);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html",
                "\r\n");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
