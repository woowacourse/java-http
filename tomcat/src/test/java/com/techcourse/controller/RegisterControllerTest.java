package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.apache.coyote.http11.Http11Header;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11StatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RegisterControllerTest {

    private final RegisterController registerController = Mockito.spy(new RegisterController());

    @Test
    @DisplayName("회원 가입 페이지 조회한다.")
    void doGet() throws IOException {
        HttpRequest request = makeRequest(new LinkedHashMap<>());
        HttpResponse response = new HttpResponse();

        registerController.doGet(request, response);

        String responseAsString = new String(response.toBytes());
        Http11ResourceFinder resourceFinder = new Http11ResourceFinder();
        Path path = resourceFinder.find("/register.html");
        String expected = new String(Files.readAllBytes(path));

        assertThat(responseAsString).contains(expected);
    }

    @Test
    @DisplayName("로그인 상태에서 회원 가입 페이지 조회시 index 페이지로 리다이렉트 한다.")
    void doGetWhenLogin() throws IOException {
        HttpRequest request = makeRequest(new LinkedHashMap<>());
        HttpResponse response = new HttpResponse();

        Mockito.when(registerController.isLogin(request)).thenReturn(true);

        registerController.doGet(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(Http11StatusCode.FOUND),
                () -> assertThat(response.findHeader("Location")).hasValue(new Http11Header("Location", "/index.html"))
        );
    }

    private HttpRequest makeRequest(LinkedHashMap<String, String> body) {
        return new HttpRequest(
                Http11Method.GET,
                "/register.html",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                body
        );
    }

    @Test
    @DisplayName("회원 가입 성공 시 index 페이지로 리다이렉트한다.")
    void doPostAndRedirect() {
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.putLast("account", "a");
        body.putLast("email", "email@email.com");
        body.putLast("password", "password");

        HttpRequest request = makeRequest(body);
        HttpResponse response = new HttpResponse();

        registerController.doPost(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(Http11StatusCode.FOUND),
                () -> assertThat(response.findHeader("Location")).hasValue(new Http11Header("Location", "/index.html"))
        );
    }

    @Test
    @DisplayName("회원 가입 성공 시 DB에 유저가 저장된다.")
    void doPostAndUserAddded() {
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.putLast("account", "a");
        body.putLast("email", "email@email.com");
        body.putLast("password", "password");

        HttpRequest request = makeRequest(body);
        HttpResponse response = new HttpResponse();

        registerController.doPost(request, response);

        Optional<User> user = InMemoryUserRepository.findByAccount("a");
        assertThat(user).isNotEmpty();
    }
}
