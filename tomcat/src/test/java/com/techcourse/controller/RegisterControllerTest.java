package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.controller.File;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final String account = "account";
    private final String password = "password";
    private final String email = "kkk@gmail.com";
    private final User user = new User(account, password, email);
    private final RegisterController registerController = new RegisterController();

    @AfterEach
    void removeUser() {
        InMemoryUserRepository.delete(user);
    }

    @DisplayName("회원 가입 시 올바르게 응답을 보낸다.")
    @Test
    void register() {
        String body = String.format("account=%s&password=%s&email=%s", account, password, email);
        int contentLength = body.getBytes().length;

        HttpRequest request = new HttpRequest(
                RequestLine.of("POST /register HTTP/1.1 "),
                new HttpHeaders(Map.of(HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(contentLength))),
                body
        );
        HttpResponse response = new HttpResponse();

        registerController.service(request, response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.FOUND,
                Map.of("Location", "/index.html")
        );
        assertAll(
                () -> assertThat(response).isEqualTo(expectedResponse),
                () -> assertThat(InMemoryUserRepository.findByAccount(account)).isNotEmpty()
        );
    }

    @DisplayName("이미 존재하는 계정일 경우 Bad Request 응답을 반환한다.")
    @Test
    void registerFail() {
        InMemoryUserRepository.save(user);
        String body = String.format("account=%s&password=%s&email=%s", account, password, email);
        int contentLength = body.getBytes().length;
        HttpRequest request = new HttpRequest(
                RequestLine.of("POST /register HTTP/1.1 "),
                new HttpHeaders(Map.of(HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(contentLength))),
                body
        );
        HttpResponse response = new HttpResponse();

        registerController.service(request, response);

        HttpResponse expectedResponse = new HttpResponse();
        expectedResponse.setTextBody("중복된 계정을 생성할 수 없습니다.");
        expectedResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("회원 가입 페이지를 올바르게 반환한다.")
    @Test
    void getRegisterPage() {
        HttpRequest request = new HttpRequest(
                RequestLine.of("GET /register HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                ""
        );
        HttpResponse response = new HttpResponse();

        registerController.service(request, response);

        HttpResponse expectedResponse = new HttpResponse();
        File file = File.of("/register.html");
        file.addToResponse(expectedResponse);
        assertThat(response).isEqualTo(expectedResponse);
    }
}
