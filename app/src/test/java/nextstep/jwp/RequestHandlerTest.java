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
    private static final String HOST = "Host: http://localhost:8080";
    private static final String CONNECTION = "Connection: keep-alive";
    private static final String DEFAULT_BODY = "account=gugu&password=password&email=hkkang%40woowahan.com";
    private static final String TEXT_HTML = "text/html";
    private static final String INDEX_HTML = "index.html";

    @Test
    @DisplayName("index 테스트")
    void index() throws IOException {
        // given
        final String httpRequest= createGetRequest(INDEX_HTML, "*/*");

        // when
        final MockSocket socket = getMockSocket(httpRequest);

        // then
        String expected = createResponseOK(INDEX_HTML, TEXT_HTML);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("login Get 테스트")
    void loginGet() throws IOException {
        final String httpRequest= createGetRequest("login", "*/*");

        final MockSocket socket = getMockSocket(httpRequest);

        // then
        String expected = createResponseOK("login.html", TEXT_HTML);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("login Post 테스트")
    void loginPost() {
        final String httpRequest = createPostRequest("login", DEFAULT_BODY);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = createResponseFound(INDEX_HTML);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("register Get 테스트")
    void registerGet() throws IOException {
        final String httpRequest= createGetRequest("register", "*/*");

        final MockSocket socket = getMockSocket(httpRequest);

        // then
        String expected = createResponseOK("register.html", TEXT_HTML);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("register Post 테스트")
    void registerPost() {
        String body = "account=test&password=password&email=mungto%40woowahan.com";

        final String httpRequest= createPostRequest("register", body);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = createResponseFound(INDEX_HTML);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("css 테스트")
    void css() throws IOException {
        final String styleCss = "css/styles.css";
        final String type = "text/css";
        final String httpRequest= createGetRequest(styleCss, type);

        final MockSocket socket = getMockSocket(httpRequest);

        // then
        String expected = createResponseOK(styleCss, type);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("javascript 테스트")
    void javascript() throws IOException {
        final String scripts = "js/scripts.js";
        final String type = "application/javascript";

        final String httpRequest= createGetRequest(scripts, type);

        final MockSocket socket = getMockSocket(httpRequest);

        // then
        String expected = createResponseOK(scripts, type);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 요청을 보내면 400이 반환된다.")
    void badRequest() {
        final String httpRequest= "";

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = String.join(NEW_LINE, "HTTP/1.1 400 Bad Request",
            "",
            "{\"message\": \"잘못된 request message 입니다.\"}");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("요청처리를 하지 않은 요청을 하면 404가 반환된다.")
    void notFound() {
        final String httpRequest= createGetRequest("getError", "*/*");

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = createResponseFound("404.html");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하는 유저로 회원가입 요청을 하면 409가 반환된다.")
    void conflict() {
        final String httpRequest= createPostRequest("register", DEFAULT_BODY);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = String.join(NEW_LINE,
            "HTTP/1.1 409 Conflict",
            "",
            "{\"message\": \"이미 존재하는 아이디 입니다.\"}"
        );
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("login 실패시 401로 리다이랙트한다.")
    void unAuthorized() {

        String body = "account=fail&password=password&email=hkkang%40woowahan.com";

        final String httpRequest= createPostRequest("login", body);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = createResponseFound("401.html");
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String getResponseBody(String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(
            new File(Objects.requireNonNull(resource).getFile()).toPath()));
    }

    private String createGetRequest(String url, String accept) {
        return String.join(NEW_LINE,
            "GET /" + url + " HTTP/1.1",
            HOST,
            CONNECTION,
            "Accept: " + accept,
            "",
            "");
    }

    private String createPostRequest(String url, String body) {
        return String.join(NEW_LINE,
            "POST /" + url + " HTTP/1.1",
            HOST,
            CONNECTION,
            "Content-Length: " + body.length(),
            "",
            body);
    }

    private String createResponseOK(String fileName, String contentType) throws IOException {
        final String responseBody = getResponseBody("static/" + fileName);

        return String.join(NEW_LINE, "HTTP/1.1 200 OK",
            "Content-Type: " + contentType + ";charset=utf-8",
            "Content-Length: "+ responseBody.getBytes().length,
            "",
            responseBody);
    }

    private String createResponseFound(String url) {
        return String.join(NEW_LINE,
            "HTTP/1.1 302 Found",
            "Location: /" + url
        );
    }

    private MockSocket getMockSocket(String httpRequest) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();
        return socket;
    }
}
