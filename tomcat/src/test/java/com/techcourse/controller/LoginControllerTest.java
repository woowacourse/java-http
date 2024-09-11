package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class LoginControllerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @BeforeEach
    void setUp() {
        sessionManager.clear();
    }

    @DisplayName("로그인 페이지를 호출한다.")
    @Test
    void successLoginGetTest() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String content = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        String contentLength = Integer.toString(content.getBytes(StandardCharsets.UTF_8).length);

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + contentLength,
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("이미 로그인 세션 쿠키가 존재하면 index.html 페이지로 리다이렉트한다.")
    @Test
    void redirectLoginGetTest() {
        User user = new User("gugu", "password", "hkkang@woowahan.com");
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.createSession();
        session.setAttribute("user", user);

        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Location: /index.html",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인에 성공하면 세션 쿠키를 포함하여 index.html 페이지로 리다이렉트한다.")
    @Test
    void successLoginPostTest() {
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        Map<String, Session> store = sessionManager.getStore();
        String sessionId = store.values().iterator()
                .next()
                .getId();

        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Set-Cookie: JSESSIONID=" + sessionId,
                "Location: /index.html",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인에 실패하면 401.html 페이지로 리다이렉트한다.")
    @Test
    void failureLoginPostTest() {
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=jazz&password=java");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Location: /401.html",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

}
