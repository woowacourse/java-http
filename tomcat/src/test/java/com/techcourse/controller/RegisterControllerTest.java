package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.fixture.HttpRequestFixture;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private RegisterController registerController;

    @BeforeEach
    void setup() {
        UserService userService = new UserService();
        registerController = new RegisterController(userService);
    }

    @DisplayName("회원가입에 성공하면 302 FOUND와 Location 헤더에 메인페이지 경로를 포함한다.")
    @Test
    void doPost() {
        //given
        Map<String, String> requestBody = Map.of(
                "account", "daon",
                "password", "1234",
                "email", "test@test.com"
        );
        HttpRequest request = HttpRequestFixture.getPostRequest("/register", requestBody);
        HttpResponse response = new HttpResponse(request);

        //when
        registerController.doPost(request, response);

        //then
        assertAll(
                () -> assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, "/index.html")
        );
    }

    @DisplayName("회원가입 중 예외를 잡으면 302 FOUND와 Location 헤더에 에러 페이지 경로를 포함한다.")
    @Test
    void doPostThrowsException() {
        //given
        Map<String, String> requestBody = Map.of(
                "account", "daon",
                "email", "test@test.com"
        );
        HttpRequest request = HttpRequestFixture.getPostRequest("/register", requestBody);
        HttpResponse response = new HttpResponse(request);

        //when
        registerController.doPost(request, response);

        //then
        assertAll(
                () -> assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }
}
