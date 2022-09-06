package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.HttpHeaderType;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.HttpRequestGenerator;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @DisplayName("GET /login 경로로 요청시 200 OK와 함께 Register 페이지를 반환한다.")
    @Test
    void performGetMethod() throws IOException {
        String request = "GET /login HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = LoginHandler.perform(httpRequest);

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_TYPE)).isEqualTo(
                        ContentType.HTML.getContentType()),
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_LENGTH)).isEqualTo("3797"),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(responseBody)
        );
    }

    @DisplayName("GET /login 경로로 요청시 Session 정보가 있다면 302 Found와 함께 index 페이지를 반환한다.")
    @Test
    void performGetMethodWithSession() throws IOException {
        String session = login();
        String request = "GET /login HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Cookie: " + session + "\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = LoginHandler.perform(httpRequest);

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.LOCATION)).isEqualTo("/index.html")
        );
    }

    @DisplayName("Put /login 경로로 로그인이 성공하면 302 Found와 함께 Index 페이지를 반환한다.")
    @Test
    void performPutMethod() throws IOException {
        String request = "POST /login HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 29\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Accept: */*\n"
                + "\n"
                + "account=rex&password=password\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = LoginHandler.perform(httpRequest);

        assertAll(
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.LOCATION)).isEqualTo("/index.html"),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.SET_COOKIE).startsWith("JSESSIONID")).isTrue()
        );
    }

    @DisplayName("Put /login 경로로 로그인이 성공하면 SessionManager에 Session을 저장한다.")
    @Test
    void performPutMethodAndSaveSession() throws IOException {
        String jSessionId = login();
        String sessionId = jSessionId.substring(11);
        SessionManager sessionManager = new SessionManager();
        Session session = sessionManager.findSession(sessionId);

        assertThat(session).isNotNull();
    }

    @DisplayName("Put /login 경로로 로그인이 실패하면 302 Found와 함께 401 페이지를 반환한다.")
    @Test
    void performPutMethodWithWrongPassword() throws IOException {
        String request = "POST /login HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 32\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Accept: */*\n"
                + "\n"
                + "account=rex&password=password123\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = LoginHandler.perform(httpRequest);

        assertAll(
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.LOCATION)).isEqualTo("/401.html")
        );
    }

    String login() throws IOException {
        String request = "POST /login HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n"
                + "Content-Length: 29\n"
                + "Content-Type: application/x-www-form-urlencoded\n"
                + "Accept: */*\n"
                + "\n"
                + "account=rex&password=password\n";

        HttpRequest httpRequest = HttpRequestGenerator.generate(request);
        HttpResponse httpResponse = LoginHandler.perform(httpRequest);
        return httpResponse.getHeader("Set-Cookie");
    }
}
