package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import http.HttpRequest;
import http.HttpResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController loginController = LoginController.instance();

    @Test
    void 로그인_GET_테스트() throws Exception {
        // given
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        HttpResponse httpResponse = loginController.service(httpRequest);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String actual = httpResponse.toResponseFormat();
        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains(expectedResponseBody)
        );
    }

    @Test
    void 로그인_POST_테스트() throws Exception {
        // given
        String sessionId = UUID.randomUUID().toString();
        SessionManager.instance().add(new Session(sessionId));
        String requestBody = "account=gugu&password=password";
        int contentLength = requestBody.getBytes().length;
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + contentLength,
                "Cookie: JSESSIONID=" + sessionId,
                "",
                requestBody);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        HttpResponse httpResponse = loginController.service(httpRequest);

        // then
        String actual = httpResponse.toResponseFormat();
        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html")
        );
    }
}
