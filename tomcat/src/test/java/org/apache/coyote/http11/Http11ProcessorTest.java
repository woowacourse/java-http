package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("1단계 - HTTP 서버 구현")
    @Nested
    class Step1 {

        @DisplayName("/로 인덱스 페이지에 접근할 수 있다.")
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

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("/index.html로 인덱스 페이지에 접근할 수 있다.")
        @Test
        void indexPage() throws IOException {
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

        @DisplayName("CSS 파일에 접근할 수 있다.")
        @Test
        void css() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Accept: text/css,*/*;q=0.1 ",
                    "Connection: keep-alive",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/css;charset=utf-8 \r\n" +
                    "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("/login으로 로그인 페이지에 접근할 수 있다.")
        @Test
        void loginPage() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login ",
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
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("2단계 - 로그인 구현")
    @Nested
    class Step2 {

        @DisplayName("로그인 성공 시, 302을 반환하고 `/index.html`로 리다이렉트 한다.")
        @Test
        void loginSuccess() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: 30 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=password ");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: http://localhost:8080/index.html ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 0 ",
                    "");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("로그인 실패 시, 302을 반환하고 `/401.html`로 리다이렉트 한다.")
        @Test
        void loginFailure() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: 35 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=wrongPassword ");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: http://localhost:8080/401.html ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 0 ",
                    "");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("/register로 회원가입 페이지에 접근할 수 있다.")
        @Test
        void registerPage() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /register ",
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
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("회원가입 성공 시, index 페이지로 리다이렉트 한다.")
        @Test
        void registerSuccess() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=password&email=hkkang%40woowahan.com");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: http://localhost:8080/index.html ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 0 ",
                    "");

            assertThat(socket.output()).isEqualTo(expected);
        }
    }
}
