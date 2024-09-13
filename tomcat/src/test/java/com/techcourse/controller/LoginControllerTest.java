package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.apache.catalina.route.RequestMapping;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class LoginControllerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final RequestMapping requestMapping = new RequestMapper();
    private final Dispatcher dispatcher = new DefaultDispatcher(requestMapping);

    @BeforeEach
    void setUp() {
        sessionManager.clear();
        requestMapping.register(new LoginController());
    }

    @DisplayName("로그인 페이지를 응답한다.")
    @Test
    void loginPage() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "", "");

        // when
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, dispatcher);
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
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

    @DisplayName("로그인 성공 시 /index.html 로 리다이렉트하고 세션 쿠키를 설정한다.")
    @Test
    void redirectWhenLoginSuccess() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");

        // when
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, dispatcher);
        processor.process(socket);

        // then
        Map<String, Session> store = sessionManager.getStore();
        String sessionId = store.values().stream()
                .findFirst()
                .map(Session::getId)
                .orElseThrow();

        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Set-Cookie: JSESSIONID=" + sessionId,
                "Content-Length: 0",
                "Location: /index.html",
                "", "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 실패 시 /401.html 로 리다이렉트한다.")
    @Test
    void failureLoginPostTest() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=pedro&password=1q2w3e4r!");

        // when
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, dispatcher);
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Content-Length: 0",
                "Location: /401.html",
                "", "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("세션 쿠키가 존재하고, 로그인 된 상태라면 index.html 페이지로 리다이렉트한다.")
    @Test
    void redirectWhenLoggedIn() {
        // given
        User user = new User("gugu", "password", "hkkang@woowahan.com");
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        session.setAttribute("user", user);

        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId(),
                "", "");

        // when
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
