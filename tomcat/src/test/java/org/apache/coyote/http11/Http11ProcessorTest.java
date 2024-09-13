package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("성공 : / 페이지 접근")
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

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/login.html", "/register.html"})
    @DisplayName("성공 : /*.html 페이지 접근")
    void loadHtml(String fileName) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET " + fileName + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + bytes.length + " \r\n" +
                "\r\n" +
                new String(bytes);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("성공 : /*.css 페이지 접근")
    void loadCss() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: text/css,*/*;q=0.1",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "Content-Length: " + bytes.length + " \r\n" +
                "\r\n" +
                new String(bytes);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("성공 : 로그인 안하고 /* 페이지 접근")
    void url(String url) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET " + url + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static" + url + ".html");
        byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + bytes.length + " \r\n" +
                "\r\n" +
                new String(bytes);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    @DisplayName("성공 : 로그인 성공 후 /* 페이지 접근 시 index 페이지로 리다이렉션")
    void urlAfterLoginSuccess(String url) throws IOException {
        // given
        String id = UUID.randomUUID().toString();
        Session session = new Session(id);
        User user = new User("aa", "aa", "aa@a.com");
        InMemoryUserRepository.save(user);
        session.setAttribute(session.getId(), user);
        SessionManager.getInstance().add(session);
        final String httpRequest = String.join("\r\n",
                "GET " + url + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + id,
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + bytes.length + " \r\n" +
                "Location: http://localhost:8080/index.html" + " \r\n" +
                "\r\n" +
                new String(bytes);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Nested
    @DisplayName("로그인 시도")
    class login {

        @ParameterizedTest
        @ValueSource(strings = {"account=gugu&password=11", "account=11&password=password"})
        @DisplayName("실패 : 아이디 혹은 비밀번호 오류로 로그인 실패 시 401 페이지 로드")
        void loginFailByPassword(String body) throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            var expected = "HTTP/1.1 401 Unauthorized \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + bytes.length + " \r\n" +
                    "\r\n" +
                    new String(bytes);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("성공 : 로그인 성공으로 index.html로 리다이렉션")
        void loginSuccess() throws IOException {
            // given
            String body = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            String JSessionId = socket.output().split("JSESSIONID=")[1].split(" \r\n")[0];
            var expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + bytes.length + " \r\n" +
                    "Location: http://localhost:8080/index.html" + " \r\n" +
                    "Set-Cookie: JSESSIONID=" + JSessionId + " \r\n" +
                    "\r\n" +
                    new String(bytes);

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("회원가입 요청")
    class register {
        @Test
        @DisplayName("성공 : 회원가입 성공으로 index.html로 리다이렉션")
        void registerSuccess() throws IOException {
            // given
            final String body = "account=kyum3&password=password&email=kyum@naver.com";
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + body.getBytes().length,
                    "",
                    body);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            var expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + bytes.length + " \r\n" +
                    "Location: http://localhost:8080/index.html" + " \r\n" +
                    "\r\n" +
                    new String(bytes);

            assertThat(socket.output()).isEqualTo(expected);
        }
    }
}
