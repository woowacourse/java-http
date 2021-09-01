package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @DisplayName("\"GET /\"로 요청을 보내면, \"Hello world!\"가 담긴 hello.html 파일을 응답한다.")
    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Length: 12 ",
            "Content-Type: text/html;charset=utf-8 ",
            "",
            "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("\"GET /index.html\" 요청을 보내면, index.html 파일을 응답한다.")
    @Test
    void index_html() throws IOException {
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
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("\"GET /login\" 요청을 보내면, login.html 파일을 응답한다.")
    @Test
    void login_html() throws IOException {
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
            "Content-Length: 3797 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("\"POST /login\" 요청을 보내서 로그인이 됐다면, index.html로 리다이렉트한다.")
    @Test
    void login() {
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
        String responseLine = "HTTP/1.1 302 Found";
        String locationHeader = "Location: index.html";
        String contentTypeHeader = "Content-Type: text/html;charset=utf-8";
        String setCookieHeader = "Set-Cookie: ";

        assertThat(socket.output().contains(responseLine)).isTrue();
        assertThat(socket.output().contains(locationHeader)).isTrue();
        assertThat(socket.output().contains(contentTypeHeader)).isTrue();
        assertThat(socket.output().contains(setCookieHeader)).isTrue();
    }

    @DisplayName("\"GET /login\" 세션으로 로그인을 성공하면, index.html로 리다이렉트 한다.")
    @Test
    void login_session() throws IOException {
        String sessionId = 로그인_성공();
        MockSocket socket;
        String httpRequest;
        RequestHandler requestHandler;

        httpRequest = String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Cookie: JSESSIONID=" + sessionId,
            "");

        socket = new MockSocket(httpRequest);
        requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("\"GET /register\" 요청을 보내면, register.html 파일을 응답한다.")
    @Test
    void register_html() throws IOException {
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
            "Content-Length: 4319 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("유효한 로그인 정보가 담긴 form 데이터와 함께 \"POST /register\" 요청을 보내면, 로그인을 진행하고, index.html로 리다이렉트 한다.")
    @Test
    void register() {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 80",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
            "Location: index.html \r\n" +
            "Content-Type: text/html;charset=utf-8 ";

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String 로그인_성공() {
        // given
        String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 30",
            "",
            "account=gugu&password=password");

        MockSocket socket = new MockSocket(httpRequest);
        RequestHandler requestHandler = new RequestHandler(socket);
        requestHandler.run();

        List<String> splits = Arrays.asList(socket.output().split(" "));
        String sessionCookie = splits.stream()
            .filter(it -> it.startsWith("JSESSIONID"))
            .findAny()
            .get();
        String sessionId = sessionCookie.split("=")[1];

        return sessionId;
    }

}
