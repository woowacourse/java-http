package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("GET 요청")
    @Nested
    class GetRequest {
        @DisplayName("[엔드포인트: /] : 콘텐츠와 200 OK 상태를 응답으로 반환한다.")
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

            assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
        }

        @DisplayName("[엔드포인트: /index.html] : GET 요청 시 index.html 콘텐츠와 200 OK 상태를 응답으로 반환한다.")
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

            assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
        }

        @DisplayName("[엔드포인트: /login] : GET 요청 시 login.html 콘텐츠와 200 OK 상태를 응답으로 반환한다.")
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
            Path path = new File(resource.getFile()).toPath();
            long fileSize = Files.size(path);
            var expected = "HTTP/1.1 200 OK \r\n" +
                           "Content-Type: text/html;charset=utf-8 \r\n" +
                           "Content-Length: " + fileSize + " \r\n" +
                           "\r\n" +
                           new String(Files.readAllBytes(path));

            assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
        }

        @DisplayName("[엔드포인트: /register] : GET 요청 시 register.html 콘텐츠와 200 OK 상태를 응답으로 반환한다.")
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
            Path path = new File(resource.getFile()).toPath();
            long fileSize = Files.size(path);
            var expected = "HTTP/1.1 200 OK \r\n" +
                           "Content-Type: text/html;charset=utf-8 \r\n" +
                           "Content-Length: " + fileSize + " \r\n" +
                           "\r\n" +
                           new String(Files.readAllBytes(path));

            assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
        }

        @DisplayName("존재하지 않는 엔드포인트에 대해 GET 요청 시 404.html으로 리다이렉트 한다.")
        @Test
        void accessWithNotExistEndPoint() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /not-exist HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = "HTTP/1.1 302 Found \r\n" +
                           "Location: /404.html \r\n" +
                           "\r\n";

            assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
        }
    }

    @DisplayName("POST 요청")
    @Nested
    class PostRequest {

        @DisplayName("[엔드포인트: /login] : 로그인에 성공하면 쿠키를 저장하고 index.html로 리다이렉트 한다.")
        @Test
        void loginSuccess() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30",
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            Assertions.assertAll(
                    () -> assertThat(response).contains("HTTP/1.1 302 Found"),
                    () -> assertThat(response).contains("Location: /index.html"),
                    () -> assertThat(response).containsPattern("Set-Cookie: JSESSIONID=[a-f0-9\\-]+")
            );
        }

        @DisplayName("[엔드포인트: /login] : 로그인에 실패하면 401.html로 리다이렉트 한다.")
        @Test
        void loginFailure() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 35",
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*",
                    "",
                    "account=unknown&password=unknown");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            Assertions.assertAll(
                    () -> assertThat(response).contains("HTTP/1.1 302 Found"),
                    () -> assertThat(response).contains("Location: /401.html")
            );
        }

        @DisplayName("[엔드포인트: /register] : 회원가입에 성공하면 index.html로 리다이렉트 한다.")
        @Test
        void registerSuccess() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 64",
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*",
                    "",
                    "account=newuser&password=password&email=newuser%40woowahan.com");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();

            Assertions.assertAll(
                    () -> assertThat(response).contains("HTTP/1.1 302 Found"),
                    () -> assertThat(response).contains("Location: /index.html")
            );
        }
    }
}
