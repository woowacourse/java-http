package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("가입 컨트롤러")
class RegisterControllerTest {

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        this.registerController = new RegisterController();
    }

    @DisplayName("가입 컨트롤러는 가입 페이지를 반환한다.")
    @Test
    void doGet() throws IOException {
        // given
        List<String> headers = List.of(
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );
        HttpRequest request = HttpRequest.of(headers, "");
        HttpResponse response = new HttpResponse();
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = Files.readString(new File(resource.getFile()).toPath());

        // when
        registerController.doGet(request, response);

        // then
        assertThat(response).extracting("body")
                .isEqualTo(expected);
    }

    @DisplayName("가입 컨트롤러는 가입이 완료되면 인덱스 페이지로 리다이렉트 한다.")
    @Test
    void doPost() {
        // given
        String body = "account=test&password=test&email=test@gmail.com";
        List<String> headers = List.of(
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded "
        );
        HttpRequest request = HttpRequest.of(headers, body);
        HttpResponse response = new HttpResponse();
        String expected = "/index.html";

        // when
        registerController.doPost(request, response);

        // then
        assertThat(response).extracting("httpHeader")
                .extracting("headers")
                .extracting("Location")
                .isEqualTo(expected);
    }
}
