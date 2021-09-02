package nextstep.jwp.web;

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
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/index.html 요청에 index.html을 포함하여 응답한다 - 성공")
    @Test
    void getIndexPage() throws IOException {
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
        final URL url = getClass().getClassLoader().getResource("static/index.html");
        final String content = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(url).getPath())));
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " "
        );
    }

    @DisplayName("GET /login 요청에 login.html 파일을 포함하여 응답한다 - 성공")
    @Test
    void getLoginPage() throws IOException {
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
        final URL url = getClass().getClassLoader().getResource("static/login.html");
        final String content = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(url).getPath())));
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " "
        );
    }

    @DisplayName("로그인 한 상태에서 GET /login 요청할 경우 index.html로 리다이렉트한다 - 성공")
    @Test
    void getLoginPageWhileLoggedIn() {
        // given
        final String jsessionid = "7f1dd9b5-ef54-4706-a07a-aaaa9d76134e";
        final String httpRequest2 = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + jsessionid,
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        final MockSocket socket2 = new MockSocket(httpRequest2);
        final RequestHandler requestHandler2 = new RequestHandler(socket2);
        requestHandler2.run();

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + jsessionid,
                "",
                "");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found ",
                "Location: /index.html"
        );
    }

    @DisplayName("로그인에 성공하면 /index.html로 리다이렉트한다 - 성공")
    @Test
    void loginSuccess() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found ",
                "Set-Cookie: JSESSIONID=",
                "Location: /index.html "
        );
    }

    @DisplayName("로그인에 실패하면 /401.html을 출력한다 - 성공")
    @Test
    void loginFail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 26 ",
                "",
                "account=gugu&password=1234");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL url = getClass().getClassLoader().getResource("static/401.html");
        final String content = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(url).getPath())));
        assertThat(socket.output()).contains(
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " "
        );
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
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL url = getClass().getClassLoader().getResource("static/register.html");
        final String content = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(url).getPath())));
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " "
        );
    }

    @DisplayName("POST /register 요청이 성공적으로 처리되면 /index.html로 리다이렉트한다 - 성공")
    @Test
    void registerSuccess() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=pobi&password=password&email=pobi%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when // then
        assertThatCode(requestHandler::run)
                .doesNotThrowAnyException();
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found ",
                "Location: /index.html "
        );
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
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL url = getClass().getClassLoader().getResource("static/css/styles.css");
        final String content = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(url).getPath())));
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css ",
                "Content-Length: " + content.getBytes().length + " "
        );
    }
}
