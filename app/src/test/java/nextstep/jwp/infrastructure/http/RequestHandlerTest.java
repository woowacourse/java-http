package nextstep.jwp.infrastructure.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.MockSocket;
import nextstep.jwp.infrastructure.http.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @DisplayName("/ 경로 조회")
    @Test
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        final URL resource = getClass().getClassLoader().getResource("static/hello.html");

        // then
        assertResponse(
            String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""),
            "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 12 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("index.html 조회")
    @Test
    void index() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/index.html");

        assertResponse(
            String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""),
            "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("로그인 페이지")
    @Test
    void loginPage() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        assertResponse(
            String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            ""),
            "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3796 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @DisplayName("로그인 성공시 index.html로 리다이렉트")
    @Test
    void successfulLogin() throws IOException {
        assertResponse(
            String.join("\r\n",
            "GET /login?account=gugu&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            ""),
            "HTTP/1.1 302 FOUND \r\n"
                + "Location: /index.html \r\n"
                + "\r\n"
        );
    }

    @DisplayName("로그인 실패시 401.html로 리다이렉트")
    @Test
    void failToLogin() {
        assertResponse(
            String.join("\r\n",
                "GET /login?account=gugu&password=password2 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""),
            "HTTP/1.1 302 FOUND \r\n"
                + "Location: /401.html \r\n"
                + "\r\n"
        );
    }
    
    @DisplayName("존재하지 않는 요청일 경우 404.html")
    @Test
    void notFoundPage() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/404.html");

        assertResponse(
            String.join("\r\n",
                "GET /notFound HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""),
            "HTTP/1.1 404 NOT FOUND \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 2426 \r\n"
                + "\r\n"
                + new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    private void assertResponse(final String httpRequest, final String httpResponse) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();

        assertThat(socket.output()).isEqualTo(httpResponse);
    }
}
