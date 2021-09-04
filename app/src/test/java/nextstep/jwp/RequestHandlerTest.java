package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHandlerTest {

    @Test
    @DisplayName("기본 화면으로 index.html을 보여준다.")
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Length: " + result.getBytes().length + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "", result);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("index.html을 보여준다.")
    void index() throws IOException {
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
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Length: " + result.getBytes().length + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "", result);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("register.html을 보여준다.")
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        final Path path = new File(resource.getPath()).toPath();
        final List<String> lines = Files.readAllLines(path);

        String result = lines.stream()
                             .map(String::valueOf)
                             .collect(Collectors.joining());

        String expected = String.join("\r\n",
                "HTTP/1.1 " + "200 OK" + " ",
                "Content-Length: " + result.getBytes().length + " ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "", result);

        assertThat(socket.output()).isEqualTo(expected);
    }


    @Test
    @DisplayName("회원가입 후 index.html을 보여준다.")
    void indexAfterRegister() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        assertTrue(socket.output().contains("index.html"));
    }

    @Test
    @DisplayName("로그인 실패 시 index.html로 돌아간다. - 아이디 오류")
    void indexAfterLoginFailureById() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu2&password=passwor");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        assertTrue(socket.output().contains("index.html"));
    }

    @Test
    @DisplayName("로그인 실패 시 index.html로 돌아간다. - 비밀번호 오류")
    void indexAfterLoginFailureByPassword() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 29",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=passwor");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        assertTrue(socket.output().contains("index.html"));
    }

    @Test
    @DisplayName("로그인 성공 시 index.html로 돌아간다.")
    void indexAfterLoginSuccess() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        assertTrue(socket.output().contains("index.html"));
    }

    @Test
    @DisplayName("로그인 상태에서 login.html GET 요청 시 index.html로 돌아간다.")
    void indexAfterLogin() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "Cookie: JSESSIONID=asdfasdfasdfasdf",
                "",
                "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        assertTrue(socket.output().contains("index.html"));
    }
}
