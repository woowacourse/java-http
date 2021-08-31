package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @DisplayName("Hello World 요청 - 성공")
    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final ApplicationContext applicationContext = ApplicationContextFactory.create();
        final RequestHandler requestHandler = new RequestHandler(socket, applicationContext);

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

    @DisplayName("index.html 파일 요청 - 성공")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final ApplicationContext applicationContext = ApplicationContextFactory.create();
        final RequestHandler requestHandler = new RequestHandler(socket, applicationContext);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        FileAccess file = new FileAccess("/index.html");

        String expected = expectedFileAccessResponse(resource, file);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지 요청 - 성공")
    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final ApplicationContext applicationContext = ApplicationContextFactory.create();
        final RequestHandler requestHandler = new RequestHandler(socket, applicationContext);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        FileAccess file = new FileAccess("/login.html");

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + file.getFile().getBytes().length + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지에서 로그인 요청 - 성공")
    @Test
    void login() throws IOException {
        // given
        String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final ApplicationContext applicationContext = ApplicationContextFactory.create();
        final RequestHandler requestHandler = new RequestHandler(socket, applicationContext);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지에서 로그인 요청 - 실패")
    @Test
    void loginFail() throws IOException {
        // given
        String requestBody = "account=pika&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes().length,
                "",
                requestBody);

        final MockSocket socket = new MockSocket(httpRequest);
        final ApplicationContext applicationContext = ApplicationContextFactory.create();
        final RequestHandler requestHandler = new RequestHandler(socket, applicationContext);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 401 Unauthorized \r\n" +
                "Location: /401.html \r\n\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입 페이지 GET 요청 - 성공")
    @Test
    void registerGET() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final ApplicationContext applicationContext = ApplicationContextFactory.create();
        final RequestHandler requestHandler = new RequestHandler(socket, applicationContext);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        FileAccess file = new FileAccess("/register.html");

        String expected = expectedFileAccessResponse(resource, file);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입 페이지 POST 요청 - 성공")
    @Test
    void registerPOST() throws IOException {
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
        final ApplicationContext applicationContext = ApplicationContextFactory.create();
        final RequestHandler requestHandler = new RequestHandler(socket, applicationContext);

        // when
        requestHandler.run();

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String expectedFileAccessResponse(URL resource, FileAccess file) throws IOException {
        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + file.getFile().getBytes().length + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
