package nextstep.jwp.web;

import nextstep.jwp.web.MockSocket;
import nextstep.jwp.web.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class RequestHandlerTest {

    @DisplayName("path가 없는 요청에 응답한다 - 성공")
    @Test
    void run() throws IOException {
        // given
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));


        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/index.html 요청에 index.html을 포함하여 응답한다 - 성공")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));


        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /login 요청에 login.html 파일을 포함하여 응답한다 - 성공")
    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원을 조회해서 로그인에 성공하면 /index.html로 리다이렉트한다 - 성공")
    @Test
    void loginSuccess() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원을 조회해서 로그인에 실패하면 /401.html로 리다이렉트한다 - 성공")
    @Test
    void loginFail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=1234 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 401 UNAUTHORIZED \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /register 요청에 register.html 파일을 포함하여 응답한다 - 성공")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("POST /register 요청에 index.html 파일을 포함하여 응답한다 - 성공")
    @Test
    void registerSuccess() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 58",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=pobi&password=password&email=pobi%40woowahan.com");
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when // then
        assertThatCode(requestHandler::run)
                .doesNotThrowAnyException();
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /css/styles.css 요청에 styles.css 파일을 포함하여 응답한다 - 성공")
    @Test
    void getCSS() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(socket.output()).isEqualTo(expected);
    }
}
