package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @DisplayName("애플리케이션을 실행한다.")
    @Test
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final FrontController frontController = new FrontController(socket);

        // when
        frontController.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("home 페이지를 조회한다.")
    @Test
    void home() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final FrontController frontController = new FrontController(socket);

        // when
        frontController.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("index 페이지를 조회한다.")
    @Nested
    class GetIndex {

        @DisplayName("확장자가 없는 경우")
        @Test
        void withoutExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("확장자가 있는 경우")
        @Test
        void withExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("login 페이지를 조회한다.")
    @Nested
    class GetLogin {

        @DisplayName("확장자가 없는 경우")
        @Test
        void withoutExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("확장자가 있는 경우")
        @Test
        void withExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("register 페이지를 조회한다.")
    @Nested
    class GetRegister {

        @DisplayName("확장자가 없는 경우")
        @Test
        void withoutExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 4319 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("확장자가 있는 경우")
        @Test
        void withExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 4319 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }
}
