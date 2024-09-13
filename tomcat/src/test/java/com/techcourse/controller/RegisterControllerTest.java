package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.apache.catalina.route.RequestMapping;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class RegisterControllerTest {

    private final RequestMapping requestMapping = new RequestMapper();
    private final Dispatcher dispatcher = new DefaultDispatcher(requestMapping);

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.deleteAll();
        requestMapping.register(new RegisterController());
    }

    @DisplayName("회원가입 페이지를 응답한다.")
    @Test
    void successRegisterGetTest() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "", "");

        // when
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, dispatcher);
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String content = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        String contentLength = Integer.toString(content.getBytes(StandardCharsets.UTF_8).length);

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: " + contentLength,
                "Content-Type: text/html;charset=utf-8",
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }


    @DisplayName("회원가입에 성공하면 index.html 페이지로 리다이렉트한다.")
    @Test
    void failureRegisterPostTest() {
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: 51",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=pedro&password=1q2w3e4r!&email=pedro@example.org");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, dispatcher);

        processor.process(socket);

        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Content-Length: 0",
                "Location: /index.html",
                "", "");

        assertThat(socket.output()).isEqualTo(expected);
    }
}
