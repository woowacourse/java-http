package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.techcourse.db.InMemoryUserRepository;

class RegisterControllerTest {
    private RegisterController registerController;
    private MockedStatic<InMemoryUserRepository> mockedRepository;

    @BeforeEach
    void setUp() {
        registerController = new RegisterController();
        mockedRepository = mockStatic(InMemoryUserRepository.class);
    }

    @AfterEach
    void clear() {
        mockedRepository.close();
    }

    @DisplayName("성공적인 회원가입 요청에 대해 302 응답을 생성한다.")
    @Test
    void loginSuccess() throws IOException {
        // given
        String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));

        // when
        String result = registerController.handle(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("회원가입이 잘못되면 400 응답을 반환한다.")
    @Test
    void loginFailedInvalidPassword() throws IOException {
        // given
        String requestBody = "account=gugu&password=password&email=";
        final String request = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));

        // when
        String result = registerController.handle(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/400.html");
        var expected = "HTTP/1.1 400 BAD REQUEST \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 2512 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(result).isEqualTo(expected);
    }
}
