package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.startup.Container;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private static final Container container = Container.from(List.of(new LoginController(), new RegisterController()));

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(container, socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 200 OK ");
        assertThat(socket.output()).contains("Content-Type: text/html;charset=utf-8 ");
        assertThat(socket.output()).contains("Content-Length: 12 ");
        assertThat(socket.output()).contains("Hello world!");
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
        final Http11Processor processor = new Http11Processor(container, socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains("HTTP/1.1 200 OK ");
        assertThat(socket.output()).contains("Content-Type: text/html;charset=utf-8 ");
        assertThat(socket.output()).contains("Content-Length: 5564 ");
        assertThat(socket.output()).contains(body);
    }

    @Nested
    class login {

        @Test
        void get_요청으로_페이지를_조회할때_로그인하지_않은_유저는_login_페이지() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(container, socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).contains("HTTP/1.1 200 OK ");
            assertThat(socket.output()).contains("Content-Type: text/html;charset=utf-8 ");
            assertThat(socket.output()).contains("Content-Length: " + body.getBytes().length + " ");
            assertThat(socket.output()).contains(body);
        }

        @Test
        void get_요청으로_페이지를_조회할때_로그인된_유저는_index_페이지_리다이렉트() throws IOException {
            // given
            final String httpRequest1 = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "",
                    "account=gugu&password=password ");

            final var socket1 = new StubSocket(httpRequest1);
            final Http11Processor processor1 = new Http11Processor(container, socket1);
            processor1.process(socket1);

            final String[] split = socket1.output().split("\r\n");
            int setCookieIndex = 1;
            if(!split[1].contains("Set-Cookie")) {
                setCookieIndex = 2;
            }
            final String jsessionid = split[setCookieIndex].split(": ")[1];

            final String httpRequest2 = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Cookie: " + jsessionid,
                    "",
                    "");
            final var socket2 = new StubSocket(httpRequest2);
            final Http11Processor processor2 = new Http11Processor(container, socket2);

            //when
            processor2.process(socket2);

            // then
            assertThat(socket2.output()).contains("302 FOUND");
            assertThat(socket2.output()).contains("Location: /index.html");
        }

        @Test
        void post_요청으로_로그인_성공시_index_페이지_리다이렉트() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "",
                    "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(container, socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("302 FOUND");
            assertThat(socket.output()).contains("Location: /index.html");
        }

        @Test
        void post_요청으로_로그인_실패시_401_페이지_리다이렉트() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "",
                    "account=stranger&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(container, socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("302 FOUND");
            assertThat(socket.output()).contains("Location: /401.html");
        }
    }

    @Nested
    class register {

        @Test
        void get_요청으로_페이지_조회시_register_페이지() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(container, socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).contains("HTTP/1.1 200 OK ");
            assertThat(socket.output()).contains("Content-Type: text/html;charset=utf-8 ");
            assertThat(socket.output()).contains("Content-Length: " + body.getBytes().length + " ");
            assertThat(socket.output()).contains(body);
        }

        @Test
        void post_요청으로_회원가입시_index_페이지_리다이렉트() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 27 ",
                    "",
                    "account=newbi&password=1234&email=email@me.com");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(container, socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("302 FOUND");
            assertThat(socket.output()).contains("Location: /index.html");
        }
    }
}
