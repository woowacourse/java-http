package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.controller.RequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @BeforeEach
    void setUp() {
        RequestMapper requestMapper = RequestMapper.getInstance();
        requestMapper.addMapping("/login", new LoginController());
        requestMapper.addMapping("/register", new RegisterController());
    }

    @DisplayName("GET / -> /index.html로 리다이렉트")
    @Test
    void defaultPath_RedirectToIndex() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found\r\n",
                "Location: /index.html\r\n"
        );
    }

    @DisplayName("GET /index.html -> index.html 내용 응답")
    @Test
    void indexHtml() throws IOException {
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

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK\r\n",
                "Content-Type: text/html;charset=utf-8\r\n",
                "Content-Length: 5564\r\n",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("GET /login -> login.html 내용 응답")
    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK\r\n",
                "Content-Type: text/html;charset=utf-8\r\n",
                "Content-Length: ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("GET /register -> regsiter.html 내용 응답")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK\r\n",
                "Content-Type: text/html;charset=utf-8\r\n",
                "Content-Length: ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("GET /non-existing-endpoint -> 404.html 응답")
    @Test
    void nonExistingEndpoint_404() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /not-existing-endpoint HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");

        assertThat(socket.output()).contains(
                "HTTP/1.1 404 Not Found\r\n",
                "Content-Type: text/html;charset=utf-8\r\n",
                "Content-Length: ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("POST /login - 회원 존재하는 경우")
    @Test
    void postLogin_UserExists() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found",
                "Set-Cookie: JSESSIONID=",
                "Location: /index.html"
        );
    }

    @DisplayName("POST /login - 회원 존재하지 않는 경우")
    @Test
    void postLogin_UserDoesNotExist() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 29",
                "",
                "account=gug&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");

        assertThat(socket.output()).contains(
                "HTTP/1.1 401 Unauthorized",
                "Content-Length: ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("POST /register - 성공")
    @Test
    void postRegister_Success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 41",
                "",
                "account=aa&email=b%40b.com&password=aaaaa");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(InMemoryUserRepository.exists("aa", "aaaaa")).isTrue();

        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found",
                "Set-Cookie: JSESSIONID=",
                "Location: /index.html"
        );
    }

    @DisplayName("POST /register - 이미 존재하는 ID")
    @Test
    void postRegister_Fail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 41",
                "",
                "account=gugu&email=b%40b.com&password=aaaaa");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/500.html");

        assertThat(socket.output()).contains(
                "HTTP/1.1 500 Internal Server Error",
                "Content-Type: text/html;charset=utf-8\r\n",
                "Content-Length: ",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("GET /js/scripts.js - 정적 파일 불러오기")
    @Test
    void name() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }
}
