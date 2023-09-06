package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
                "Content-Type: text/plain;charset=utf-8 ",
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
    class Login {

        @Test
        void get() throws IOException {
            //given
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 3797 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            //when
            processor.process(socket);

            //then
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void post() {
            //given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            //when
            processor.process(socket);

            //then
            var location = "Location: /index.html \r\n";
            var cookie = "Set-Cookie: JSESSIONID=.*\r\n";
            String response = socket.output();
            assertAll(
                    () -> assertThat(response).startsWith("HTTP/1.1 302 Found"),
                    () -> assertThat(response).contains(location),
                    () -> assertThat(response).containsPattern(cookie)
            );
        }

        @Test
        void postUnauthorized() throws IOException {
            //given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 35",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=gugu&password=wrongPassword");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            //when
            processor.process(socket);

            //then
            final var URI = getClass().getClassLoader().getResource("static/401.html");
            final var expected = "HTTP/1.1 401 Unauthorized \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 2426 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(URI.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void setCookie() {
            //given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            //when
            processor.process(socket);

            //then
            var expectedLocation = "Location: /index.html \r\n";
            var expectedCookie = "Set-Cookie: JSESSIONID=.*\r\n";

            String response = socket.output();
            assertAll(
                    () -> assertThat(response).startsWith("HTTP/1.1 302 Found"),
                    () -> assertThat(response).contains(expectedLocation),
                    () -> assertThat(response).containsPattern(expectedCookie)
            );
        }

        @Test
        void alreadyLoggedIn() {
            //given
            final var sessionId = createSessionId();

            final var httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Cookie: JSESSIONID=" + sessionId,
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            //when
            processor.process(socket);

            //then
            final var expected = "HTTP/1.1 302 Found \r\n" +
                    "Location: /index.html \r\n";
            assertThat(socket.output()).isEqualTo(expected);
        }

        private String createSessionId() {
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);
            processor.process(socket);
            return socket.output().split("JSESSIONID=")[1];
        }

    }

    @Nested
    class Register {

        @Test
        void get() throws IOException {
            //given
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 4319 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            //when
            processor.process(socket);

            //then
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void post() {
            //given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "",
                    "account=rosie&email=rosie@zipgo.pet&password=password");
            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            //when
            processor.process(socket);

            //then
            final var expected = "HTTP/1.1 302 Found \r\n" +
                    "Location: /index.html \r\n";
            assertThat(socket.output()).isEqualTo(expected);
        }

    }


}
