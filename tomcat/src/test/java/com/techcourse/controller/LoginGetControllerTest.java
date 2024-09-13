package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.message.HttpHeaderName;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestBody;
import org.apache.coyote.http11.message.request.HttpUrl;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginGetControllerTest {

    private LoginGetController controller = new LoginGetController();

    @Test
    @DisplayName("세션이 없는 경우 로그인 페이지를 반환한다.")
    void getLoginPageWithoutSessionTest() throws IOException {
        // given
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        HttpRequest request =
                new HttpRequest(HttpMethod.GET, new HttpUrl("/login"), requestHeaders, new HttpRequestBody());

        // when
        HttpResponse response = controller.handle(request);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.CONTENT_TYPE))
                        .contains("text/html;charset=utf-8"),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.CONTENT_LENGTH))
                        .contains(String.valueOf(body.length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @Test
    @DisplayName("세션이 있는 경우 index 페이지로 리다이렉트한다.")
    void getLoginPageWithSessionTest() throws IOException {
        // given
        UUID uuid = UUID.randomUUID();
        SessionManager.getInstance().add(new Session(uuid.toString()));
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setHeader(HttpHeaderName.HOST, "localhost:8080");
        requestHeaders.setHeader(HttpHeaderName.CONNECTION, "keep-alive");
        requestHeaders.setHeader(HttpHeaderName.COOKIE, "JSESSIONID=" + uuid);
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        HttpRequest request =
                new HttpRequest(HttpMethod.GET, new HttpUrl("/login"), requestHeaders, new HttpRequestBody());

        // when
        HttpResponse response = controller.handle(request);

        // then
        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getFieldByHeaderName(HttpHeaderName.LOCATION))
                        .contains("http://localhost:8080/index.html")
        );
    }
}
