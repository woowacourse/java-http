package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.controller.dto.HttpResponseEntity;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest {

    @DisplayName("로그인 데이터를 조회한다.")
    @Test
    void searchUserData() {
        //given
        String target = "gugu";
        Map<String, String> params = Map.of("account", target, "password", "password");
        User user = InMemoryUserRepository.findByAccount(target)
                .orElseThrow();
        UserController userController = new UserController();

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.body()).isEqualTo(user),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/index.html")
        );
    }

    @DisplayName("유효하지 않는 account로 조회하면 401.html로 리다이렉트 한다.")
    @Test
    void searchUserDataInvalidAccount() {
        //given
        Map<String, String> params = Map.of("account", "daon", "password", "password");
        UserController userController = new UserController();

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.body()).isNull(),
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }

    @DisplayName("유효하지 않는 비밀번호로 조회하면 401.html로 리다이렉트 한다.")
    @Test
    void searchUserDataInvalidPassword() {
        //given
        String target = "pass";
        Map<String, String> params = Map.of("account", "gugu", "password", target);
        UserController userController = new UserController();

        //when
        HttpResponseEntity<User> result = userController.searchUserData(params);

        //then
        assertAll(
                () -> assertThat(result.body()).isNull(),
                () -> assertThat(result.httpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(result.headers()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }
}
