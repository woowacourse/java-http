package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestFixture;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @Test
    @DisplayName("회원가입을 수행한다.")
    void doPost() throws IOException {
        // given
        final HttpRequest request = HttpRequestFixture.registerPost();
        final HttpResponse response = new HttpResponse();

        // when
        registerController.doPost(request, response);
        final List<String> headerStrings = response.getHeaders()
                .stream()
                .map(HttpHeader::getHeaderAsString)
                .toList();

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 302 Found "),
                () -> assertThat(headerStrings.getLast()).isEqualTo("Location: /index.html ")
        );
    }

    @Test
    @DisplayName("회원가입 페이지를 반환한다.")
    void doGet() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.registerGet();
        final HttpResponse response = new HttpResponse();

        // when
        registerController.doGet(request, response);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final var body = Files.readAllBytes(new File(resource.getFile()).toPath());
        assertThat(response.getBody()).isEqualTo(new String(body));
    }

    @Test
    @DisplayName("이미 존재하는 아이디를 입력하면 401.html을 반환한다.")
    void doPostFailed() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.duplicatedIdRegisterPost();
        final HttpResponse response = new HttpResponse();

        // when
        registerController.doPost(request, response);
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
}