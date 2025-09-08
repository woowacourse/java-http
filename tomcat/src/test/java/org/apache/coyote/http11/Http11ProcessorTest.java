package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("빈 HTTP 요청일 경우 500을 반환한다.")
    @Test
    void testEmptyHttpRequest() throws IOException {
        // given
        final String httpRequest = "";

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/500.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error ",
                "Content-Type: text/html; charset=utf-8 ",
                String.format("Content-Length: %s ", responseBody.getBytes().length),
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("정적 리소스 GET 요청 테스트")
    @Nested
    class HttpGetTest {

        @DisplayName("GET /index.html : 상태코드 200, index.html 파일을 반환한다.")
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
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    String.format("Content-Length: %s ", responseBody.getBytes().length),
                    "",
                    responseBody);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("GET / : 상태코드 200과 index.html을 반환한다.")
        @Test
        void root() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET / HTTP/1.1 ",
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
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    String.format("Content-Length: %s ", responseBody.getBytes().length),
                    "",
                    responseBody);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("GET /login : 상태코드 200과 login.html을 반환한다.")
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
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    String.format("Content-Length: %s ", responseBody.getBytes().length),
                    "",
                    responseBody);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("GET /register : 상태코드 200과 register.html을 반환한다.")
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
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    String.format("Content-Length: %s ", responseBody.getBytes().length),
                    "",
                    responseBody);

            assertThat(socket.output()).isEqualTo(expected);
        }


        @DisplayName("존재하지 않는 리소스를 요청하면 404를 반환한다.")
        @Test
        void testNotFoundResource() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /nonexistent.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/404.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = String.join("\r\n",
                    "HTTP/1.1 404 Not Found ",
                    "Content-Type: text/html; charset=utf-8 ",
                    String.format("Content-Length: %s ", responseBody.getBytes().length),
                    "",
                    responseBody);

            assertThat(socket.output()).isEqualTo(expected);
        }

    }

    @DisplayName("POST /login : 로그인 테스트")
    @Nested
    class LoginTest {

        @DisplayName("로그인 정보가 일치하면 /login 경로로 302 응답을 보낸다.")
        @Test
        void testLoginSuccess() {
            // given
            final String requestBody = String.format("account=gugu&password=password");
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    String.format("Content-Length: %d", requestBody.getBytes().length),
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*",
                    "",
                    requestBody);

            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: http://localhost:8080/login ",
                    "Content-Length: 0 ");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("로그인 정보가 저장된 정보와 일치하지 않으면 401을 반환한다.")
        @Test
        void testLoginFail() throws IOException {
            // given
            final String requestBody = String.format("account=gugu&password=invalidPassword");
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    String.format("Content-Length: %d", requestBody.getBytes().length),
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*",
                    "",
                    requestBody);

            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            final String expected = String.join("\r\n",
                    "HTTP/1.1 401 Unauthorized ",
                    "Content-Type: text/html; charset=utf-8 ",
                    String.format("Content-Length: %s ", responseBody.getBytes().length),
                    "",
                    responseBody);

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("POST /register: 회원가입 테스트")
    @Nested
    class RegisterTest {

        @DisplayName("회원 가입을 성공하면 새로운 유저를 생성하고 /index.html 경로로 302 응답을 보낸다.")
        @Test
        void testRegisterSuccess() {
            // given
            final String newAccount = "norang";
            final String requestBody = String.format("account=%s&email=hkkang%%40woowahan.com&password=password",
                    newAccount);
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    String.format("Content-Length: %d", requestBody.getBytes().length),
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*",
                    "",
                    requestBody);

            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final String expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: http://localhost:8080/index.html ",
                    "Content-Length: 0 ");
            assertAll(
                    () -> assertThat(InMemoryUserRepository.findByAccount(newAccount)).isPresent(),
                    () -> assertThat(socket.output()).isEqualTo(expected)
            );
        }
    }

    @Test
    @Disabled("사용하지 않는 초기 테스트")
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
}
