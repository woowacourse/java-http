package org.apache.controller;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.controller.FileReader.FileReader;
import org.apache.coyote.FixtureFactory;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("로그인에 성공하면 index.html 페이지를 바디로 갖고, httpStatusCode가 302인 response를 생성한다.")
    void login_post_success() {
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");
        Request request = FixtureFactory.getPostRequest("/login", DEFAULT_HEADERS, new RequestBody(body));
        Response response = new Response();

        LoginController loginController = new LoginController();
        loginController.service(request, response);

        String expectedLine = "HTTP/1.1 302 FOUND";
        String expectedHeader = "Location: /index.html";

        assertAll(
                () -> assertThat(response.getResponseBytes()).startsWith(expectedLine.getBytes()),
                () -> assertThat(response.getResponseBytes()).contains(expectedHeader.getBytes())
        );
    }

    @Test
    @DisplayName("로그인에 실패하면 401.html 페이지를 바디로 갖고, httpStatusCode가 401인 response를 생성한다.")
    void login_post_fail() {
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu12");
        body.put("password", "password123");

        Request request = FixtureFactory.getPostRequest("/login", DEFAULT_HEADERS, new RequestBody(body));
        Response response = new Response();

        LoginController loginController = new LoginController();
        loginController.service(request, response);

        String expectedLine = "HTTP/1.1 401 UNAUTHORIZED";
        String expectedHeader = "Location: /401.html";

        assertAll(
                () -> assertThat(response.getResponseBytes()).startsWith(expectedLine.getBytes()),
                () -> assertThat(response.getResponseBytes()).contains(expectedHeader.getBytes())
        );
    }

    @Test
    @DisplayName("로그인 페이지에 접속할 수 있다")
    void login_get() throws IOException, URISyntaxException {
        Request request = FixtureFactory.getGetRequest("/login", DEFAULT_HEADERS);
        Response response = new Response();

        LoginController loginController = new LoginController();
        loginController.service(request, response);

        Path path = Path.of(FileReader.class.getResource("/static/login.html").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        String expectedLine = "HTTP/1.1 200 OK";

        assertAll(
                () -> assertThat(response.getResponseBytes()).startsWith(expectedLine.getBytes()),
                () -> assertThat(response.getResponseBytes()).contains(expectedBody.getBytes())
        );
    }
}
