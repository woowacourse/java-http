package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
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
        RequestLine requestLine = RequestLine.from("POST /register HTTP/1.1 ");
        String body = "account=gugu&password=password&email=hkkang@woowahan.com";
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(body), new RequestBody(body));
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.service(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: index.html ";

        assertThat(httpResponse.serialize()).contains(expectedResponseLine, expectedLocationHeader);
    }

    @DisplayName("회원가입이 잘못되면 400.html로 리다이랙트한다.")
    @Test
    void loginFailedInvalidPassword() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("POST /register HTTP/1.1 ");
        String body = "account=gugu&password=password&email=";
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(body), new RequestBody(body));
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.service(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: 400.html ";

        assertThat(httpResponse.serialize()).contains(expectedResponseLine, expectedLocationHeader);
    }

    @DisplayName("회원가입 화면을 응답한다.")
    @Test
    void registerPage() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("GET /register HTTP/1.1 ");
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(null), new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        registerController.service(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: register.html ";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

    private HttpHeaders createHeaders(String body) {
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        if (Objects.nonNull(body)) {
            headers.setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        }
        return headers;
    }
}
