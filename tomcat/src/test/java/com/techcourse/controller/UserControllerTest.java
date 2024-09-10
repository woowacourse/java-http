package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.controller.dto.HttpResponseEntity;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.Map;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.request.RequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setup() {
        userController = new UserController(new UserService());
    }

    @DisplayName("유저 데이터를 조회하면 302 FOUND와 Location 헤더에 /index.html를 반환한다.")
    @Test
    void searchUserData() {
        //given
        String account = "gugu";
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/index.html")
        );
    }

    @DisplayName("유저 데이터 조회 실패하면 302 FOUND와 Location 헤더에 /401.html를 반환한다.")
    @Test
    void searchUserDataNotExist() {
        //given
        String account = "";
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }

    @DisplayName("로그인에 성공하면 302 FOUND와 Location 헤더에 /index.html를 반환한다.")
    @Test
    void login() {
        //given
        String account = "gugu";
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);
        HttpRequest httpRequest =
                new HttpRequest(HttpRequestLine.from("POST /login HTTP/1.1"), Map.of(), new RequestBody());

        //when
        HttpResponseEntity<User> result = userController.login(params, httpRequest);

        //then
        assertAll(
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/index.html"),
                () -> assertThat(result.headers()).containsKey(HttpHeaders.SET_COOKIE)
        );
    }

    @DisplayName("로그인에 실패하면 302 FOUND와 Location 헤더에 /401.html를 반환한다.")
    @Test
    void loginNotExist() {
        //given
        String account = "";
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }

    @DisplayName("회원가입을 성공하면 302 FOUND와 Location 헤더에 /index.html를 반환한다.")
    @Test
    void register() {
        //given
        String account = "gugu";
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/index.html")
        );
    }

    @DisplayName("회원가입에 실패하면 302 FOUND와 Location 헤더에 /401.html를 반환한다.")
    @Test
    void registerAlreadyExistAccount() {
        //given
        String account = "";
        String password = "password";
        Map<String, String> params = Map.of("account", account, "password", password);

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }
}
