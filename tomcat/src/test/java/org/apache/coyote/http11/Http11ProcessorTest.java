package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

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
        final String httpRequest = String.join("\r\n",
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
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() {
        // given
        final String requestBody = """
                {"account": "new-user", "password": "new-password", "email": "new-user@example.com"}""";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/json ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Redirect ");

        final String loginRequest = String.join("\r\n",
                "GET /login?account=new-user&password=new-password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var loginSocket = new StubSocket(loginRequest);
        final Http11Processor loginProcessor = new Http11Processor(loginSocket);

        loginProcessor.process(loginSocket);

        assertThat(loginSocket.output()).startsWith("HTTP/1.1 302 Redirect ");
    }

    @Test
    void registerWithFormUrlEncodedBody() {
        // given
        final String requestBody = "account=%EC%84%B1%EC%97%B4&email=ert2154%40naver.com&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Redirect ");

        final String loginRequest = String.join("\r\n",
                "GET /login?account=%EC%84%B1%EC%97%B4&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var loginSocket = new StubSocket(loginRequest);
        final Http11Processor loginProcessor = new Http11Processor(loginSocket);

        loginProcessor.process(loginSocket);

        assertThat(loginSocket.output()).startsWith("HTTP/1.1 302 Redirect ");
    }
}
