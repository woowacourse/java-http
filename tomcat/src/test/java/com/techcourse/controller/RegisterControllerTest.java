package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.HttpRequestFixture;
import org.apache.catalina.response.ContentType;
import org.apache.catalina.response.Header;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @DisplayName("register.html을 응답한다.")
    @Test
    void doGet() throws IOException {
        HttpResponse response = new HttpResponse();
        registerController.doGet(HttpRequestFixture.getHttpGetRequest("/register.html"), response);

        File file = new File(getClass().getClassLoader().getResource("static/register.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("OK"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getBody()).isEqualTo(body)
        );
    }

    @DisplayName("회원가입에 성공하면 index.html을 응답한다.")
    @Test
    void doPost() throws IOException {
        HttpResponse response = new HttpResponse();
        registerController.doPost(HttpRequestFixture.getHttpPostRequest("/register.html", "account=tenny&password=password&email=hkkang%40woowahan.com"), response);

        File file = new File(getClass().getClassLoader().getResource("static/index.html").getFile());
        String body = new String(Files.readAllBytes(file.toPath()));
        assertAll(
                () -> assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200),
                () -> assertThat(response.getStatusLine().getStatusMessage()).isEqualTo("OK"),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_TYPE)).isEqualTo(ContentType.HTML.value()),
                () -> assertThat(response.getHeaders().get(Header.CONTENT_LENGTH)).isEqualTo(String.valueOf(body.getBytes().length)),
                () -> assertThat(response.getBody()).isEqualTo(body),
                () -> assertThat(InMemoryUserRepository.findByAccount("gugu")).isPresent()
        );
    }
}
