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

class RegisterControllerTest {

    @DisplayName("정상적으로 회원가입이 되는지 테스트한다.")
    @Test
    void doPost() {
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + "account=kim&password=password&email=a%40naver.com".getBytes().length,
                "",
                "account=kim&password=password&email=a%40naver.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        assertThat(socket.output()).contains("Location: /index.html");
    }

    @DisplayName("회원가입 페이지를 정상적으로 띄우는지 테스트한다.")
    @Test
    void doGet() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        processor.process(socket);

        assertThat(socket.output()).contains(
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }
}