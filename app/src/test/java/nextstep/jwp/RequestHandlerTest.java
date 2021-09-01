package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Nested
    @DisplayName("GET 요청을 하면")
    class Get {

        @Test
        @DisplayName("/로 접속시 Hello world! 를 보여준다.")
        void run() {
            // given
            final MockSocket socket = new MockSocket();
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 12 ",
                    "",
                    "Hello world!");
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("/index.html 로 접속시, index 페이지를 보여준다.")
        void indexHtml() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 5564 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("/index 로 접속시, index 페이지를 보여준다.")
        void index() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /index HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 5564 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("/login 로 접속시, login 페이지를 보여준다.")
        void login() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 3797 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("/register 로 접속시, register 페이지를 보여준다.")
        void register() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 4319 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("css 요청을 하면 해당 정적파일을 불러온다.")
        void css() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Accept: text/css,*/*;q=0.1 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);
            final int startOfResource = 0;
            final int endOfResource = 3000;

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            String expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/css;charset=utf-8 \r\n" +
                    "Content-Length: 211991 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output().substring(startOfResource, endOfResource)).isEqualTo(
                    expected.substring(startOfResource, endOfResource));
        }
    }

    @Disabled
    @Nested
    @DisplayName("쿼리스트링으로 로그인을 하면")
    class Login {

        @Test
        @DisplayName("성공하면 index 페이지를 보여준다.")
        void success() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login?account=gugu&password=password HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 5564 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패하면 401 페이지를 보여준다.")
        void fail() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /login?account=gugu&password=PASSword HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            String expected = "HTTP/1.1 401 Unauthorized \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 2426 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("POST로 로그인을 하면")
    class PostLogin {

        @Test
        @DisplayName("성공하면 index 페이지를 보여준다.")
        void success() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=password");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 5564 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        @DisplayName("실패하면 401 페이지를 보여준다.")
        void fail() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 30 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=PASSword");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            String expected = "HTTP/1.1 401 Unauthorized \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 2426 \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("등록을 하면")
    class Register {

        @Test
        @DisplayName("성공하면 index 페이지를 보여준다.")
        void success() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=password&email=hkkang%40woowahan.com ");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: 5564 \r\n" +
                    "Location: /index.html \r\n" +
                    "\r\n" +
                    new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }
    }

}
