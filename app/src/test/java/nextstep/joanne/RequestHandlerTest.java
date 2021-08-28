package nextstep.joanne;

import nextstep.joanne.handler.RequestHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String responseBody = "Hello world!";
        String expected = to200OkWithHtml(responseBody);
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String to200OkWithHtml(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/index", "/login", "/login.html", "/register", "/register.html"})
    @DisplayName("정적 요청에 응답한다.")
    void preserveStaticResource(String uri) throws IOException {
        // given
        final String httpRequest = makeGetRequest(uri);

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        if (!uri.contains(".")) {
            uri += ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        String expected = to200OkWithHtml(new String(Files.readAllBytes(
                new File(Objects.requireNonNull(resource).getFile()).toPath())));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("css 파일 요청 시 응답한다.")
    void cssRequest() {
        //given
        final String uri = "/css/styles.css";
        final String httpRequest = makeGetRequest(uri);
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        //when
        requestHandler.run();
        final String actual = socket.output();

        //then
        String expected = to200OkWithCSS();
        assertThat(actual).startsWith(expected);
    }

    private String makeGetRequest(String uri) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    private String to200OkWithCSS() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: 211991 ",
                "",
                "");
    }

    @Test
    @DisplayName("login 성공 시 log에 유저 정보를 나타내고 index.html로 리다이렉트한다.")
    void loginWithQueryString() throws IOException {
        // given
        final String httpRequest = makePostRequest("/login.html", "account=gugu&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = to302FoundWithHtml(new String(
                Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath())));
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String to302FoundWithHtml(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String makePostRequest(String uri, String body) {
        return String.join("\r\n",
                "POST " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body,
                "",
                "");
    }

    @Test
    @DisplayName("login 실패 시 log에 account, password를 나타내고 401.html로 리다이렉트한다.")
    void loginWithQueryStringWhenWrongAccount() throws IOException {
        // given
        final String httpRequest = makePostRequest("/login", "account=merong&password=merong");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = to401UnauthorizedWithHtml(new String(Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath())));
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String to401UnauthorizedWithHtml(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    @DisplayName("회원가입 성공 시 index로 리다이렉트한다.")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = makePostRequest("/register.html",
                "account=joanne&password=password&email=hkkang%40woowahan.com");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = to302FoundWithHtml(new String(
                Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath())));
        assertThat(socket.output()).isEqualTo(expected);
    }
}
