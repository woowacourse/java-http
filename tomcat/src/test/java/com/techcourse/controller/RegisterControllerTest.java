package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.http.Header;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpSessionWrapper;
import jakarta.http.HttpStatus;
import jakarta.http.HttpVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterControllerTest {

    @Test
    @DisplayName("GET 요청을 처리할 수 있다.")
    void doGet() throws Exception {
        RegisterController registerController = new RegisterController();
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);

        registerController.doGet(Mockito.mock(HttpRequest.class), response);

        Assertions.assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getBody()).isNotEmpty()
        );
    }

    @Test
    @DisplayName("POST 요청을 처리할 수 있다. (회원가입 처리)")
    void doPost() throws Exception {
        RegisterController registerController = new RegisterController();
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);
        SimpleBody body = createBody("lee", "1234", "email@email.com");

        HttpRequest request = HttpRequest.createHttpRequest(
                "POST /register HTTP/1.1",
                Header.empty(),
                body,
                HttpVersion.HTTP_1_1,
                Mockito.mock(HttpSessionWrapper.class)
        );

        registerController.doPost(request, response);

        Optional<User> found = InMemoryUserRepository.findByAccount("lee");
        assertThat(found).isNotEmpty();
    }

    @Test
    @DisplayName("account는 필수 값이다.")
    void requireAccount() throws Exception {
        RegisterController registerController = new RegisterController();
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);
        SimpleBody body = createBody(null, "1234", "email@email.com");

        HttpRequest request = HttpRequest.createHttpRequest(
                "POST /register HTTP/1.1",
                Header.empty(),
                body,
                HttpVersion.HTTP_1_1,
                Mockito.mock(HttpSessionWrapper.class)
        );

        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("account 값은 필수입니다.");
    }

    @Test
    @DisplayName("password는 필수 값이다.")
    void requirePassword() throws Exception {
        RegisterController registerController = new RegisterController();
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);
        SimpleBody body = createBody("lee", null, "email@email.com");

        HttpRequest request = HttpRequest.createHttpRequest(
                "POST /register HTTP/1.1",
                Header.empty(),
                body,
                HttpVersion.HTTP_1_1,
                Mockito.mock(HttpSessionWrapper.class)
        );

        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password 값은 필수입니다.");
    }

    @Test
    @DisplayName("email은 필수 값이다.")
    void requireEmail() throws Exception {
        RegisterController registerController = new RegisterController();
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);
        SimpleBody body = createBody("lee", "1234", null);

        HttpRequest request = HttpRequest.createHttpRequest(
                "POST /register HTTP/1.1",
                Header.empty(),
                body,
                HttpVersion.HTTP_1_1,
                Mockito.mock(HttpSessionWrapper.class)
        );

        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email 값은 필수입니다.");
    }

    private SimpleBody createBody(String account, String password, String email) {
        HashMap<String, String> data = new HashMap<>();
        data.put("account", account);
        data.put("password", password);
        data.put("email", email);

        return new SimpleBody(data);
    }
}
