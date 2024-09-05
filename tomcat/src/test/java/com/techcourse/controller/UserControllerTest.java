package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.controller.dto.Response;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest {

    @DisplayName("로그인 데이터를 조회한다.")
    @Test
    void searchUserData() {
        //given
        String target = "account";
        Map<String, String> params = Map.of(target, "gugu", "password", "password");
        UserController userController = new UserController();

        //when
        Response<User> result = userController.searchUserData(params);

        //then
        assertThat(result.response().getAccount()).isEqualTo(params.get(target));
    }

    @DisplayName("유효하지 않는 account로 조회하면 예외가 발생한다.")
    @Test
    void searchUserDataInvalidAccount() {
        //given
        Map<String, String> params = Map.of("account", "daon", "password", "password");
        UserController userController = new UserController();

        //when //then
        assertThatThrownBy(() -> userController.searchUserData(params))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessageContaining("일치하는 계정이 존재하지 않습니다.");
    }

    @DisplayName("유효하지 않는 비밀번호로 조회하면 예외가 발생한다.")
    @Test
    void searchUserDataInvalidPassword() {
        //given
        String target = "pass";
        Map<String, String> params = Map.of("account", "gugu", "password", target);
        UserController userController = new UserController();

        //when //then
        assertThatThrownBy(() -> userController.searchUserData(params))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }
}
