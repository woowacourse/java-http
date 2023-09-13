package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.FileReader;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final Controller controller = new RegisterController();
    private final FileReader fileReader = new FileReader();

    @Test
    @DisplayName("/register 에 GET 요청을 했을 때 /register.html 이 반환된다.")
    void doGet() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
            "GET /register HTTP/1.1 ",
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
        final String fileContent = fileReader.readStaticFile("/register.html");
        assertThat(message).contains(
            "HTTP/1.1", "200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: " + fileContent.getBytes().length
        );
    }

    @Test
    @DisplayName("/register 에 POST 요청을 했을 때, 회원가입에 성공하면 FOUND와 index.html Location 이 반환된다.")
    void doPost_registerSuccess() throws Exception {
        // given
        final String body = "account=gugu2&password=password2";
        final String httpRequestMessage = String.join("\r\n",
            "POST /register HTTP/1.1 ",
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
            "Location: /index.html"
        );
    }

    @Test
    @DisplayName("/register 에 POST 요청을 했을 때, 회원가입에 실패하면 CONFLICT와 401.html 바디가 반환된다.")
    void doPost_regsiterFail() throws Exception {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequestMessage = String.join("\r\n",
            "POST /register HTTP/1.1 ",
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
        final String fileContent = fileReader.readStaticFile("/401.html");
        assertThat(message).contains(
            "HTTP/1.1", "409 CONFLICT",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: " + fileContent.getBytes().length
        );
    }
}
