package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Http11ProcessorTest {

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

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Nested
    class 로그인 {

        @Test
        void 페이지_접속시_200_OK_를_반환한다() throws IOException {
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
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void 성공_시_302_FOUND를_반환한다() {
            // given
            final String content = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + content.getBytes().length,
                    "",
                    content);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = "HTTP/1.1 302 FOUND \r\nLocation: /index.html";
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void 실패_시_401_UNAUTHORIZE를_반환한다() throws IOException {
            // given
            final String content = "account=gugu&password=password2";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + content.getBytes().length,
                    "",
                    content);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            var expected = "HTTP/1.1 401 UNAUTHORIZED \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;
            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    class 회원가입 {

        @Test
        void 페이지_접속시_200_OK_를_반환한다() throws IOException {
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
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void 성공_시_302_FOUND를_반환한다() {
            // given
            final String content = "account=gugu2&password=password&email=hkkang@woowahan.com";
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + content.getBytes().length,
                    "",
                    content);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            var expected = "HTTP/1.1 302 FOUND \r\nLocation: /index.html";
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void 닉네임_중복시_409_CONFLICT를_반환한다() throws IOException {
            // given
            final String content = "account=gugu&password=password&email=hkkang@woowahan.com";
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + content.getBytes().length,
                    "",
                    content);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/409.html");
            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            var expected = "HTTP/1.1 409 CONFLICT \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    responseBody;
            assertThat(socket.output()).isEqualTo(expected);
        }
    }
}
