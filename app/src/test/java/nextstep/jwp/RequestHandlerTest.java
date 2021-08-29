package nextstep.jwp;

import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    private static final String NEW_LINE = "\r\n";

    @Test
    @DisplayName("index 테스트")
    void index() throws IOException {
        // given
        final String httpRequest= String.join(NEW_LINE,
                "GET /index.html HTTP/1.1",
                "Host: http://localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final String responseBody = getResponseBody("static/index.html");

        String expected = String.join(NEW_LINE, "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: "+ responseBody.getBytes().length + " ",
            "",
            responseBody);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("login Get 테스트")
    void loginGet() throws IOException {
        final String httpRequest= String.join("\r\n",
            "GET /login HTTP/1.1",
            "Host: http://localhost:8080",
            "Connection: keep-alive",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String responseBody = getResponseBody("static/login.html");

        String expected = String.join(NEW_LINE, "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: "+ responseBody.getBytes().length + " ",
            "",
            responseBody);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("login Post 테스트")
    void loginPost() {

        final String httpRequest= String.join(NEW_LINE,
            "POST /login HTTP/1.1",
            "Host: http://localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 58",
            "",
            "account=gugu&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        String expected = String.join(NEW_LINE,
            "HTTP/1.1 302 Found ",
            "Location: http://localhost:8080/index.html"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("register Get 테스트")
    void registerGet() throws IOException {
        final String httpRequest= String.join(NEW_LINE,
            "GET /register HTTP/1.1",
            "Host: http://localhost:8080",
            "Connection: keep-alive",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String responseBody = getResponseBody("static/register.html");

        String expected = String.join(NEW_LINE, "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: "+ responseBody.getBytes().length + " ",
            "",
            responseBody);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("register Post 테스트")
    void registerPost() {

        final String httpRequest= String.join(NEW_LINE,
            "POST /register HTTP/1.1",
            "Host: http://localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 58",
            "",
            "account=abcd&password=password&email=mungto%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        String expected = String.join(NEW_LINE,
            "HTTP/1.1 302 Found ",
            "Location: http://localhost:8080/index.html"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("css 테스트")
    void css() throws IOException {
        final String httpRequest= String.join(NEW_LINE,
            "GET /css/styles.css HTTP/1.1",
            "Host: http://localhost:8080",
            "Connection: keep-alive",
            "Accept: text/css",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String responseBody = getResponseBody("static/css/styles.css");

        String expected = String.join(NEW_LINE, "HTTP/1.1 200 OK ",
            "Content-Type: text/css;charset=utf-8 ",
            "Content-Length: "+ responseBody.getBytes().length + " ",
            "",
            responseBody);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("javascript 테스트")
    void javascript() throws IOException {
        final String httpRequest= String.join(NEW_LINE,
            "GET /js/scripts.js HTTP/1.1",
            "Host: http://localhost:8080",
            "Connection: keep-alive",
            "Accept: application/javascript",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String responseBody = getResponseBody("static/js/scripts.js");

        String expected = String.join(NEW_LINE, "HTTP/1.1 200 OK ",
            "Content-Type: application/javascript;charset=utf-8 ",
            "Content-Length: "+ responseBody.getBytes().length + " ",
            "",
            responseBody);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("비어있는 request가 날라가면 400이 반환된다.")
    void noMessage() {
        final String httpRequest= "";

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        String expected = String.join(NEW_LINE, "HTTP/1.1 400 Bad Request ",
            "Content-Type: application/javascript;charset=utf-8 ",
            "",
            "{\"message\": \"잘못된 request message 입니다.\"}");
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String getResponseBody(String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(
            new File(Objects.requireNonNull(resource).getFile()).toPath()));
    }
}
