package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class LoginControllerTest {

    @DisplayName("로그인이 정상적으로 되는지 테스트한다.")
    @Test
    void doPost() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + "account=gugu&password=password".getBytes().length,
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        processor.process(socket);

        assertThat(socket.output()).contains("Location: /index.html");
    }

    @DisplayName("로그인 페이지가 정상적으로 뜨는지 확인한다.")
    @Test
    void doGet() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        processor.process(socket);

        assertThat(socket.output()).contains(
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }
}