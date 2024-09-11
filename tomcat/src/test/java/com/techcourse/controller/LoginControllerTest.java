package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestFixture;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    @DisplayName("로그인을 수행한다.")
    void doPost() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.loginPost();
        final HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);
        final List<String> headerStrings = response.getHeaders()
                .stream()
                .map(HttpHeader::getHeaderAsString)
                .toList();

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(headerStrings.getFirst()).startsWith("Set-Cookie: JSESSIONID="),
                () -> assertThat(headerStrings.getLast()).isEqualTo("Location: /index.html ")
        );
    }

    @Test
    @DisplayName("로그인 페이지를 반환한다.")
    void doGet() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.loginGet();
        final HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(request, response);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final var body = Files.readAllBytes(new File(resource.getFile()).toPath());
        assertThat(response.getBody()).isEqualTo(new String(body));
    }

    @Test
    @DisplayName("존재하지 않는 아이디를 입력하면 401.html을 반환한다.")
    void doPostFailedWithInvalidId() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.invalidIdLoginPost();
        final HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);
        final List<String> headerStrings = response.getHeaders()
                .stream()
                .map(HttpHeader::getHeaderAsString)
                .toList();

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(headerStrings.getFirst()).isEqualTo("Location: /401.html ")
        );
    }

    @Test
    @DisplayName("올바르지 않은 비밀번호를 입력하면 401.html을 반환한다.")
    void doPostFailedWithInvalidPassword() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.invalidPasswordLoginPost();
        final HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);
        final List<String> headerStrings = response.getHeaders()
                .stream()
                .map(HttpHeader::getHeaderAsString)
                .toList();

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(headerStrings.getFirst()).isEqualTo("Location: /401.html ")
        );
    }

    @Test
    @DisplayName("이미 로그인 되어 있다면 로그인 페이지 접근 시, 인덱스 페이지로 리다이렉트한다.")
    void doGetRedirectWhenAlreadyLogin() throws Exception {
        SessionManager.add(new Session("cookie-value"));

        final HttpRequest request = HttpRequestFixture.loginGetWithSessionId();
        final HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(request, response);
        final List<String> headerStrings = response.getHeaders()
                .stream()
                .map(HttpHeader::getHeaderAsString)
                .toList();

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(headerStrings.getFirst()).startsWith("Set-Cookie: JSESSIONID="),
                () -> assertThat(headerStrings.getLast()).isEqualTo("Location: /index.html ")
        );
    }
}