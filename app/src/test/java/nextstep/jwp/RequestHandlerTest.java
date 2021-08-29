package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.web.RequestHandler;
import nextstep.jwp.web.http.request.HttpRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @DisplayName("/로 접근시 Hello world! 가 출력된다.")
    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 12",
            "",
            "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/index.html GET 요청 성공")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = HttpRequestFactory.getIndexHtml();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String path = "static/index.html";
        final String responseBody = readResource(path);
        final int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: " + contentLength + "\r\n" +
            "\r\n" +
            responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login GET 요청 성공")
    @Test
    void getLogin() throws IOException {
        // given
        final String httpRequest = HttpRequestFactory.getLogin();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String path = "static/login.html";
        final String responseBody = readResource(path);
        final int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: " + contentLength + "\r\n" +
            "\r\n" +
            responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login POST 요청 성공")
    @Test
    void postLogin() {
        // given
        final String httpRequest = HttpRequestFactory.postLoginWithRequestBody();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /index.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("유효하지 않은 로그인 시 401.html로 리다이렉 한다.")
    @Test
    void postLogin_invalidUser_redirect401() {
        // given
        final String httpRequest = HttpRequestFactory.postLoginWithInvalidUser();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /401.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register GET 요청 성공")
    @Test
    void getRegister() throws IOException {
        // given
        final String httpRequest = HttpRequestFactory.getRegister();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String path = "static/register.html";
        final String responseBody = readResource(path);
        final int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: " + contentLength + "\r\n" +
            "\r\n" +
            responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register POST 요청 성공")
    @Test
    void postRegister() {
        // given
        final String httpRequest = HttpRequestFactory.postRegisterUser();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /index.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
        assertThat(InMemoryUserRepository.findByAccount("newUser")).isNotNull();
    }

    @DisplayName("유효하지 않은 리소스 요청시 404로 리다이렉트한다.")
    @Test
    void invalidUrl_redirect404() {
        // given
        final String httpRequest = HttpRequestFactory.invalidUrl();

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found\r\n" +
            "Location: /404.html\r\n" +
            "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String readResource(String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(
            Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
