package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.StatusLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @DisplayName("GET 요청 시 JSESSIONID 쿠키 존재 여부에 따라 적절한 페이지를 반환해야 한다.")
    @Test
    void doGet_WithJSessionId_ReturnsIndexHtml() throws IOException {
        // JSESSIONID 쿠키가 있는 경우
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

        assertThat(statusLine.getStatusCode()).isEqualTo(StatusCode.valueOf(302));
    }


    @DisplayName("GET 요청 시 JSESSIONID 쿠키가 없으면 login.html을 반환해야 한다.")
    @Test
    void doGet_WithoutJSessionId_ReturnsLoginHtml() throws IOException {
        // JSESSIONID 쿠키가 없는 경우
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
        assertThat(statusLine.getStatusCode()).isEqualTo(StatusCode.valueOf(200));
    }

    @DisplayName("POST 요청 시 잘못된 계정으로 로그인 시도 시 401 에러를 반환해야 한다.")
    @Test
    void doPost_InvalidAccount() throws IOException {
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

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(StatusCode.valueOf(401));
    }

    @DisplayName("POST 요청 시 잘못된 비밀번호로 로그인 시도 시 401 에러를 반환해야 한다.")
    @Test
    void doPost_InvalidPassword() throws IOException {
        String requestString = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost",
                "Content-Length: 27",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=test&password=wrongPassword"
        );

        InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
        Http11Request request = Http11Request.from(inputStream);
        Http11Response response = Http11Response.of(request);

        loginController.doPost(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(StatusCode.valueOf(401));
    }

}
