package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {
    @Nested
    @DisplayName("GET 요청에 대한 테스트")
    class GetTest {
        @Test
        @DisplayName("/ url로 접속하면 hello world가 로드된다.")
        void process() {
            // given
            final var socket = new StubSocket();
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Length: 12 ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "",
                    "Hello world!");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("/index.html url로 접속하면 대시보드 페이지가 로드된다.")
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
                    "Content-Length: 5564 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("회원가입 화면에 접속하면 회원가입 페이지가 로드된다.")
        void registerGet() throws IOException {
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
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Length: 4319 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("현재 저장된 세션이 없을 때, 로그인 화면에 접속하면 로그인 페이지가 로드된다.")
        void loginGet() throws IOException {
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
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Length: 2849 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("현재 저장된 세션이 존재할 때, 로그인 화면에 접속하면 /index.html로 리다이렉트된다.")
        void loginGet_already_login() {
            // given
            String sessionId = createSessionId();
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Cookie: JSESSIONID=" + sessionId,
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);
            // then
            var expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Length: 0 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Location: /index.html \r\n" +
                    "\r\n";

            assertThat(socket.output()).isEqualTo(expected);
        }

        private String createSessionId() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            return socket.output().split("JSESSIONID=")[1].split(" \r\n")[0].trim();
        }

        @Test
        @DisplayName("유효하지 않은 url로 접속할 경우 404.html 파일이 로드된다.")
        void load_404() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /test HTTP/1.1 ",
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
            var expected = "HTTP/1.1 404 Not Found \r\n" +
                    "Content-Length: 2426 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("POST 요청에 대한 테스트")
    class PostTest {
        @Test
        @DisplayName("/register에 POST 요청을 보내면 index.html로 리다이렉트하는 302 응답이 반환된다.")
        void register() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 57 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=amaranth&email=amaranth%40naver.com&password=test");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);
            var registerActual = socket.output();

            loginAfterRegister(socket);
            var loginActual = socket.output();

            // then
            var expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Length: 0 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Location: /index.html \r\n" +
                    "\r\n";

            assertThat(registerActual).isEqualTo(expected);

            var expectedStart = "HTTP/1.1 302 Found \r\n";
            assertThat(loginActual).startsWith(expectedStart);
        }

        private void loginAfterRegister(StubSocket socket) {
            final String loginRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=amaranth&password=test");

            final Http11Processor processor = new Http11Processor(new StubSocket(loginRequest));

            processor.process(socket);
        }

        @Test
        @DisplayName("/login에 존재하지 않는 회원의 정보를 담은 POST 요청을 보내면 401.html 파일이 로드된다.")
        void login_fail() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 57 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=amaranth&password=test");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);
            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            var expected = "HTTP/1.1 401 Unauthorization \r\n" +
                    "Content-Length: 2426 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("/login에 POST 요청을 보내면 Set-Cookie에 세션 아이디가 포함된 302 응답이 반환된다.")
        void login() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);
            // then
            var expectedStart = "HTTP/1.1 302 Found \r\n";
            var expectedHeaderProperty = "Set-Cookie: JSESSIONID=";
            var expectedEnd = "Content-Length: 0 \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Location: /index.html \r\n" +
                    "\r\n";
            var actual = socket.output();
            Assertions.assertAll(
                    () -> assertThat(actual).startsWith(expectedStart),
                    () -> assertThat(actual).contains(expectedHeaderProperty),
                    () -> assertThat(actual).endsWith(expectedEnd)
            );
        }

    }


}
