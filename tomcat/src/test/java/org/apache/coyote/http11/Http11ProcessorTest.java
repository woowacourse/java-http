package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    @Disabled
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

    @Nested
    @DisplayName("정적 리소스 조회")
    class GetStaticResourceTest {

        @Test
        @DisplayName("index.html 페이지 조회")
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

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("style.css 파일 테스트")
        void css() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            String contentBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/css;charset=utf-8 \r\n" +
                    String.format("Content-Length: %d \r\n", contentBody.getBytes().length) +
                    "\r\n" +
                    contentBody;

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("로그인 뷰 테스트")
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
            String contentBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    String.format("Content-Length: %d \r\n", contentBody.getBytes().length) +
                    "\r\n" +
                    contentBody;

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("로그인 로직 성공 테스트")
        void loginSuccess() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login?account=gugu&password=password HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = "HTTP/1.1 301 FOUND \r\n" +
                    "Location: /index.html \r\n" +
                    "\r\n";

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("로그인 로직 실패 테스트")
        void loginFailed() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login?account=gugu&password=wrong HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = "HTTP/1.1 301 FOUND \r\n" +
                    "Location: /401.html \r\n" +
                    "\r\n";

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class ResisterTest {

        @Test
        @DisplayName("회원가입 뷰 테스트")
        void registerView() throws IOException {
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
            String contentBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    String.format("Content-Length: %d \r\n", contentBody.getBytes().length) +
                    "\r\n" +
                    contentBody;

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("회원가입 로직 성공 테스트")
        void registerSuccess() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "account=HoeSeong123&password=eyeTwinkle&email=chorong@wooteco.com");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = "HTTP/1.1 301 FOUND \r\n" +
                    "Location: /index.html \r\n" +
                    "\r\n";

            assertThat(socket.output()).isEqualTo(expected);
        }
    }
}
