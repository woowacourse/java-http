package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
        registerController = RegisterController.getInstance();
        mockedRepository = mockStatic(InMemoryUserRepository.class);
    }

    @AfterEach
    void clear() {
        mockedRepository.close();
    }

    @DisplayName("성공적인 회원가입 요청에 대해 index.html로 리다이랙트한다.")
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
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: index.html ";

        assertThat(httpResponse.serialize()).contains(expectedResponseLine, expectedLocationHeader);
    }

    @DisplayName("회원가입이 잘못되면 400.html로 리다이랙트한다.")
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
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: 400.html ";

        assertThat(httpResponse.serialize()).contains(expectedResponseLine, expectedLocationHeader);
    }

    @DisplayName("회원가입 화면을 응답한다.")
    @Test
    void registerPage() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: register.html ";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }
}
