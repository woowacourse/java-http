package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @DisplayName("회원 가입 시 올바르게 응답을 보낸다.")
    @Test
    void register() {
        String account = "account";
        User user = new User(account, "password", "kkk@gmail.com");
        String body = "account=account&password=password&email=kkk@gmail.com";
        int contentLength = body.getBytes().length;

        HttpRequest request = new HttpRequest(
                RequestLine.of("POST /register HTTP/1.1 "),
                new HttpHeaders(Map.of("Content-Length", String.valueOf(contentLength))),
                new Session("new session"),
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
        InMemoryUserRepository.delete(user);
    }
}
