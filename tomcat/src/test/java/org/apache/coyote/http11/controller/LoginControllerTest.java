package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import nextstep.jwp.controller.LoginController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.FileReader;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final Controller controller = new LoginController();
    private final FileReader fileReader = new FileReader();

    @Test
    @DisplayName("/login 에 GET 요청을 했을 때, 세션이 없다면 /login.html 이 반환된다.")
    void doGet() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Accept: text/html",
            "",
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when
        controller.service(httpRequest, httpResponse);

        // then
        final String message = httpResponse.convertToMessage();
        final String fileContent = fileReader.readStaticFile("/login.html");
        assertThat(message).contains(
            "HTTP/1.1", "200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: " + fileContent.getBytes().length
        );
    }

    @Test
    @DisplayName("/login 에 GET 요청을 했을 때, 세션이 있다면 /index.html 과 302가 반환된다.")
    void doGet_alreadyLoggedIn() throws Exception {
        // given
        final String id = UUID.randomUUID().toString();
        SessionManager.add(new Session(id));

        final String httpRequestMessage = String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Accept: text/html",
            "Cookie: JSESSIONID=" + id,
            "",
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when
        controller.service(httpRequest, httpResponse);

        // then
        final String message = httpResponse.convertToMessage();
        assertThat(message).contains(
            "HTTP/1.1", "302 FOUND",
            "Location: /index.html"
        );
    }

    @Test
    @DisplayName("/login 에 POST 요청을 했을 때, 로그인에 성공하면 Location, Set-Cookie 헤더와 302가 반환된다.")
    void doPost_loginSuccess() throws Exception {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequestMessage = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + body.getBytes().length,
            "",
            body,
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when
        controller.service(httpRequest, httpResponse);

        // then
        final String message = httpResponse.convertToMessage();
        assertThat(message).contains(
            "HTTP/1.1", "302 FOUND",
            "Location: /index.html",
            "Set-Cookie: "
        );
    }

    @Test
    @DisplayName("/login 에 POST 요청을 했을 때, 로그인에 실패하면 Location 401.html과 302가 반환된다.")
    void doPost_loginFail() throws Exception {
        // given
        final String body = "account=gugu2&password=password";
        final String httpRequestMessage = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + body.getBytes().length,
            "",
            body,
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when
        controller.service(httpRequest, httpResponse);

        // then
        final String message = httpResponse.convertToMessage();
        assertThat(message).contains(
            "HTTP/1.1", "302 FOUND",
            "Location: /401.html"
        );
    }
}
