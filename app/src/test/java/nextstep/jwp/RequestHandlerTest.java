package nextstep.jwp;

import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {
    private static final String LOGIN_HTML = "static/login.html";
    private static final String LOGIN_CONTENT_LENGTH = "3797";
    private static final String INDEX_HTML = "static/index.html";
    private static final String REDIRECT_INDEX_HTML = "index.html";
    private static final String INDEX_CONTENT_LENGTH = "5564";
    private static final String REGISTER_HTML = "static/register.html";
    private static final String REGISTER_CONTENT_LENGTH = "4319";

    @Test
    @DisplayName("인덱스 페이지 접속")
    void getIndex() throws IOException {
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
        String expected = getHttpResponse(HttpStatus.OK_200.toString(), INDEX_HTML, INDEX_CONTENT_LENGTH);
        String output = socket.output();
        assertThat(output).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 페이지 접속")
    void getLogin() throws IOException {
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
        String expected = getHttpResponse(HttpStatus.OK_200.toString(), LOGIN_HTML, LOGIN_CONTENT_LENGTH);
        String output = socket.output();
        assertThat(output).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 - 쿠키 존재하지 않음")
    void getLoginWithInvalidCookie() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = getHttpResponse(HttpStatus.OK_200.toString(), LOGIN_HTML, LOGIN_CONTENT_LENGTH);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 쿠키 반환")
    void postLogin() throws IOException {
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
        String expected = getHttpRedirectResponse(HttpStatus.FOUND_302.toString(), REDIRECT_INDEX_HTML);
        String actual = socket.output();
        assertThat(actual.contains("Set-Cookie") && actual.contains("JSESSIONID")).isTrue();

        String actualExceptCookie = Arrays.stream(actual.split("\r\n"))
                .filter(line -> !line.contains("Set-Cookie"))
                .collect(Collectors.joining("\r\n"));
        assertThat(actualExceptCookie).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 실패 - 401")
    void postLoginException() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 25",
                "",
                "account=gugu&password=pwd");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = getHttpResponse(HttpStatus.UNAUTHORIZED_401.toString(), "static/401.html", "2426");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원가입 페이지 접속")
    void getRegister() throws IOException {
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
        String expected = getHttpResponse(HttpStatus.OK_200.toString(), REGISTER_HTML, REGISTER_CONTENT_LENGTH);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원가입")
    void postRegister() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: 43",
                "Connection: keep-alive ",
                "",
                "account=user&password=pwd&email=email@email");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = getHttpRedirectResponse(HttpStatus.FOUND_302.toString(), REDIRECT_INDEX_HTML);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("회원가입 실패 - 401")
    void postRegisterException() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: 43 ",
                "Connection: keep-alive ",
                "",
                "account=user&password=pwd");
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = getHttpResponse(HttpStatus.UNAUTHORIZED_401.toString(), "static/401.html", "2426");
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String getHttpResponse(String httpStatus, String resource, String length) throws IOException {
        final URL url = getClass().getClassLoader().getResource(resource);
        return "HTTP/1.1 " + httpStatus + "\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + length + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(url.getFile()).toPath()));
    }

    private String getHttpRedirectResponse(String httpStatus, String redirect) throws IOException {
        final URL url = getClass().getClassLoader().getResource(redirect);
        return "HTTP/1.1 " + httpStatus + "\r\n" +
                "Location: " + "http://localhost:8080/" + redirect + " " + "\r\n" +
                "\r\n" +
                null;
    }
}
