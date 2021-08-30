package nextstep.jwp;

import nextstep.jwp.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {
    private static final String LOGIN_HTML = "static/login.html";
    private static final String LOGIN_CONTENT_LENGTH = "3797";
    private static final String INDEX_HTML = "static/index.html";
    private static final String INDEX_CONTENT_LENGTH = "5564";
    private static final String REGISTER_HTML = "static/register.html";
    private static final String REGISTER_CONTENT_LENGTH = "4319";

    @Test
    @DisplayName("인덱스 페이지에 접속한다.")
    void getIndex() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        String expected = getPageResponse(HttpStatus.OK, INDEX_HTML, INDEX_CONTENT_LENGTH);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 페이지에 접속한다.")
    void getLogin() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        String expected = getPageResponse(HttpStatus.OK, LOGIN_HTML, LOGIN_CONTENT_LENGTH);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인한다.")
    void postLogin() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = getPageResponse(HttpStatus.FOUND, INDEX_HTML, INDEX_CONTENT_LENGTH);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원가입 페이지에 접속한다.")
    void getRegister() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        String expected = getPageResponse(HttpStatus.OK, REGISTER_HTML, REGISTER_CONTENT_LENGTH);
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String getPageResponse(HttpStatus httpStatus, String resource, String length) throws IOException {
        final URL url = getClass().getClassLoader().getResource(resource);
        return "HTTP/1.1 "+ httpStatus.toString() +"\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + length + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }
}
