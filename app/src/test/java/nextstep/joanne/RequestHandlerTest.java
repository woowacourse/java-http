package nextstep.joanne;

import nextstep.joanne.server.handler.HandlerMapping;
import nextstep.joanne.server.handler.RequestHandler;
import nextstep.joanne.server.handler.controller.ControllerFactory;
import nextstep.joanne.server.http.request.HttpRequestParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import static nextstep.Fixture.makeGetRequest;
import static nextstep.Fixture.makeGetRequestWithCookie;
import static nextstep.Fixture.makeGetRequestWithCookieWithoutSessionId;
import static nextstep.Fixture.makePostRequest;
import static org.assertj.core.api.Assertions.assertThat;

class RequestHandlerTest {

    // given
    MockSocket socket;
    RequestHandler requestHandler;

    @AfterEach
    void tearDown() throws IOException {
        socket.close();
    }

    @Test
    void run() throws IOException {
        socket = new MockSocket();
        requestHandler = new RequestHandler(
                socket,
                new HandlerMapping(ControllerFactory.addControllers()),
                new HttpRequestParser()
        );

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = to200OkWithHtml(new String(Files.readAllBytes(
                new File(Objects.requireNonNull(resource).getFile()).toPath())));
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String to200OkWithHtml(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/index", "/login", "/login.html", "/register", "/register.html"})
    @DisplayName("정적 요청에 응답한다.")
    void preserveStaticResource(String uri) throws IOException {
        // given
        final String httpRequest = makeGetRequestWithCookie(uri);
        init(httpRequest);

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

    private void init(String httpRequest) {
        socket = new MockSocket(httpRequest);
        requestHandler = new RequestHandler(
                socket,
                new HandlerMapping(ControllerFactory.addControllers()),
                new HttpRequestParser()
        );
    }

    @Test
    @DisplayName("css 파일 요청 시 응답한다.")
    void cssRequest() {
        //given
        final String uri = "/css/styles.css";
        final String httpRequest = makeGetRequest(uri);
        init(httpRequest);

        //when
        requestHandler.run();
        final String actual = socket.output();

        //then
        String expected = to200OkWithCSS();
        assertThat(actual).startsWith(expected);
    }

    private String to200OkWithCSS() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css ",
                "Content-Length: 211991 ",
                "");
    }

    @Test
    @DisplayName("login 성공 시 log에 유저 정보를 나타내고 index.html로 리다이렉트한다.")
    void loginWithQueryString() throws IOException {
        // given
        final String httpRequest = makePostRequest("/login.html", "account=gugu&password=password");
        init(httpRequest);

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
                "Location: /index.html ",
                "Content-Type: text/html ",
                "",
                "");
    }

    @Test
    @DisplayName("login 실패 시 log에 account, password를 나타내고 401.html로 리다이렉트한다.")
    void loginWithQueryStringWhenWrongAccount() throws IOException {
        // given
        final String httpRequest = makePostRequest("/login", "account=merong&password=merong");
        init(httpRequest);
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
                "Content-Type: text/html ",
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
        init(httpRequest);
        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = to302FoundWithHtml(new String(
                Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath())));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("html을 찾지 못한 경우 404 페이지를 응답한다.")
    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = makeGetRequest("/joanne.html");
        init(httpRequest);
        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expected = to404NotFoundWithHtml(new String(
                Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath())));
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String to404NotFoundWithHtml(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    @DisplayName("헤더에 Cookie가 있고, JSESSION 아이디가 없는 경우 응답에 JSESSIONID를 포함한다.")
    @Test
    void cookie() {
        // given
        final String httpRequest = makeGetRequestWithCookieWithoutSessionId("/index.html");
        init(httpRequest);
        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).contains("Set-Cookie");
        assertThat(socket.output()).contains("JSESSIONID");
    }

    @DisplayName("헤더에 Cookie가 있고, JSESSION 아이디가 없는 경우 응답에 JSESSIONID를 포함한다.")
    @Test
    void cookieWithSessionId() {
        // given
        final String httpRequest = makeGetRequestWithCookie("/index.html");
        init(httpRequest);
        // when
        requestHandler.run();

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie");
        assertThat(socket.output()).doesNotContain("JSESSIONID");
    }
}
