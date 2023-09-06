package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.servlet.DispatcherServletManager;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Nested
    class Index {

        @Test
        void fullUrl() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new DispatcherServletManager());

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            var expected = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 5564\r\n" +
                    "Content-Type: text/html;charset=utf-8\r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void defaultUrl() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET / HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new DispatcherServletManager());

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            var expected = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 5564\r\n" +
                    "Content-Type: text/html;charset=utf-8\r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    class Login {

        @Test
        void getLogin() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new DispatcherServletManager());

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            var expected = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 3797\r\n" +
                    "Content-Type: text/html;charset=utf-8\r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void postLogin() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 33",
                    "Content-Type: application/x-www-form-urlencoded",
                    "",
                    "account=gugu&password=password");

            InMemoryUserRepository.save(new User(1L, "gugu", "password", "ttset@dsffd.com"));
            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new DispatcherServletManager());

            // when
            processor.process(socket);

            // then
            var expected = List.of("HTTP/1.1 302 Found", "Location: /index.html", "Set-Cookie: JSESSIONID=");
            assertThat(socket.output()).contains(expected);
        }

        @Test
        void postLoginWithInvalidPassword() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 33",
                    "Content-Type: application/x-www-form-urlencoded",
                    "",
                    "account=gugu&password=invalid");

            InMemoryUserRepository.save(new User(1L, "gugu", "password", "ttset@dsffd.com"));
            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new DispatcherServletManager());

            // when
            processor.process(socket);

            // then
            var expected = List.of("HTTP/1.1 302 Found", "Location: /401.html");
            var shouldNotContain = List.of("Set-Cookie: JSESSIONID=");
            assertAll(
                    () -> assertThat(socket.output()).contains(expected),
                    () -> assertThat(socket.output()).doesNotContain(shouldNotContain)
            );
        }
    }

    @Nested
    class Register {

        @Test
        void getRegister() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, new DispatcherServletManager());

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            var expected = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 4319\r\n" +
                    "Content-Type: text/html;charset=utf-8\r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void postRegister() {
            // given
            final String body = "account=test&password=test&email=test@test";
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                    "Content-Type: application/x-www-form-urlencoded",
                    "",
                    body);

            final var socket = new StubSocket(httpRequest);

            final Http11Processor processor = new Http11Processor(socket, new DispatcherServletManager());

            // when
            processor.process(socket);

            // then
            var expected = List.of("HTTP/1.1 302 Found", "Location: /index.html");
            assertThat(socket.output()).contains(expected);
        }
    }
}
