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
        assertThat(socket.output()).contains("HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
    }

    @Test
    @DisplayName("루트 요청에 대해 index.html 페이지 응답")
    void index() throws IOException {
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

        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                String.format("Content-Length: %d \r\n", responseBody.getBytes().length),
                "\r\n",
                responseBody
        );
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

            String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    String.format("Content-Length: %d \r\n", responseBody.getBytes().length),
                    "\r\n",
                    responseBody
            );
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

            assertThat(socket.output()).contains("HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/css;charset=utf-8 \r\n",
                    String.format("Content-Length: %d \r\n", contentBody.getBytes().length),
                    "\r\n",
                    contentBody
            );
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

            assertThat(socket.output()).contains("HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    String.format("Content-Length: %d \r\n", contentBody.getBytes().length),
                    "\r\n",
                    contentBody);
        }

        @Test
        @DisplayName("로그인 로직 성공 테스트")
        void loginSuccess() throws IOException {
            // given
            String requestBody = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + requestBody.getBytes().length,
                    "",
                    requestBody);
            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("HTTP/1.1 301 FOUND \r\n",
                    "Location: /index.html \r\n",
                    "\r\n");
        }

        @Test
        @DisplayName("로그인 로직 실패 테스트")
        void loginFailed() throws IOException {
            String requestBody = "account=gugu&password=wrong";
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + requestBody.getBytes().length,
                    "",
                    requestBody);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("HTTP/1.1 301 FOUND \r\n",
                    "Location: /401.html \r\n",
                    "\r\n");
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

            assertThat(socket.output()).contains("HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    String.format("Content-Length: %d \r\n", contentBody.getBytes().length),
                    "\r\n",
                    contentBody);
        }

        @Test
        @DisplayName("회원가입 로직 성공 테스트")
        void registerSuccess() throws IOException {
            // given
            String requestBody = "account=HoeSeong123&password=eyeTwinkle&email=chorong@wooteco.com";
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Content-Length: " + requestBody.getBytes().length,
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    requestBody);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("HTTP/1.1 301 FOUND \r\n",
                    "Location: /index.html \r\n",
                    "\r\n");
        }
    }
}
