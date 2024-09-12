package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @DisplayName("GET 요청 시 JSESSIONID 쿠키 존재하면 index.html로 302 리다이렉트 된다.")
    @Test
    void doGet1() throws IOException {
        String sessionId = "test-session-id";
        String requestString = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost",
                "Cookie: JSESSIONID=" + sessionId,
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                ""
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        loginController.doGet(request, response);
        StatusLine statusLine = response.getStatusLine();

        String path = "src/main/resources/static/index.html";
        String htmlString = new String(Files.readAllBytes(Paths.get(path)));

        assertThat(statusLine.getStatusCode()).isEqualTo(StatusCode.valueOf(302));
        assertThat(response.getResponseBody().getBody()).isEqualTo(htmlString);
    }


    @DisplayName("GET 요청 시 JSESSIONID 쿠키가 없으면 login.html을 반환해야 한다.")
    @Test
    void doGet2() throws IOException {
        String requestString = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost",
                "Content-Length: 0",
                "Content-Type: application/x-www-form-urlencoded",
                ""
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        loginController.doGet(request, response);
        StatusLine statusLine = response.getStatusLine();

        String path = "src/main/resources/static/login.html";
        String htmlString = new String(Files.readAllBytes(Paths.get(path)));

        assertThat(statusLine.getStatusCode()).isEqualTo(StatusCode.valueOf(200));
        assertThat(response.getResponseHeader().getHeader("Content-Type")).isEqualTo("text/html");
        assertThat(response.getResponseBody().getBody()).isEqualTo(htmlString);
    }

    @DisplayName("POST 요청 시 잘못된 계정으로 로그인 시도 시 401 에러를 반환해야 한다.")
    @Test
    void doGet3() throws IOException {
        String body = "account=wrongUser&password=1234";
        String requestString = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        loginController.doPost(request, response);

        String path = "src/main/resources/static/401.html";
        String htmlString = new String(Files.readAllBytes(Paths.get(path)));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(StatusCode.valueOf(401));
        assertThat(response.getResponseBody().getBody()).isEqualTo(htmlString);
    }

    @DisplayName("POST 요청 시 잘못된 비밀번호로 로그인 시도 시 401 에러를 반환해야 한다.")
    @Test
    void doGet4() throws IOException {
        String body = "account=test&password=wrongPassword";
        String requestString = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        loginController.doPost(request, response);

        String path = "src/main/resources/static/401.html";
        String htmlString = new String(Files.readAllBytes(Paths.get(path)));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(StatusCode.valueOf(401));
        assertThat(response.getResponseBody().getBody()).isEqualTo(htmlString);
    }
}
