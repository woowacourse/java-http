package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {
    @Test
    void doPost() {
        // given
        RegisterController registerController = new RegisterController();
        String account = "account";
        String password = "password";
        String email = "email";

        HttpRequest request = new HttpRequest(
                List.of("POST / HTTP/1.1"),
                "account=" + account + "&password=" + password + "&email=" + email
        );
        HttpResponse response = new HttpResponse();

        // when
        registerController.doPost(request, response);

        // then
        assertAll(
                () -> assertThat(response.getReponse()).contains("302 FOUND"),
                () -> assertThat(response.getReponse()).contains("Location: /index.html")
        );
    }

    @Test
    void doGet() {
        // given
        RegisterController registerController = new RegisterController();
        HttpRequest request = new HttpRequest(List.of("GET / HTTP/1.1"), "");
        HttpResponse response = new HttpResponse();

        // when
        registerController.doGet(request, response);

        // then
        assertAll(
                () -> assertThat(response.getReponse()).contains("200 OK"),
                () -> assertThat(response.getReponse()).contains("<title>회원가입</title>")
        );
    }
}
