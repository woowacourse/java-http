package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.InvalidRegisterException;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;

class UserServiceTest {

    private User user;
    private UserService userService;

    @BeforeEach
    void init() {
        userService = new UserService();

        user = new User("validUser", "correctPassword", "correctEmail");
        InMemoryUserRepository.save(user);
    }

    @AfterEach
    void delete() {
        InMemoryUserRepository.deleteByAccount(user.getAccount());
    }

    @DisplayName("계정과 비밀번호로 로그인에 성공한다.")
    @Test
    void login() {
        // given
        String account = user.getAccount();
        String password = user.getPassword();

        // when & then
        assertThatCode(() -> userService.login(account, password)).doesNotThrowAnyException();
    }

    @DisplayName("잘못된 비밀번호로 로그인에 실패한다.")
    @Test
    void cannotLoginWithWrongPassword() {
        // given
        String account = user.getAccount();
        String password = "wrongPassword";

        // when & then
        assertThatThrownBy(() -> userService.login(account, password))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Wrong password");
    }

    @DisplayName("잘못된 비밀번호로 로그인에 실패한다.")
    @Test
    void cannotLoginWithUnknownAccount() {
        // given
        String account = "unknownAccount";
        String password = user.getPassword();

        // when & then
        assertThatThrownBy(() -> userService.login(account, password))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Cannot find account");
    }

    @DisplayName("누락된 정보가 있어서 로그인에 실패한다.")
    @Test
    void cannotLoginWithoutInformation() {
        // given
        String account = "";
        String password = user.getPassword();

        // when & then
        assertThatThrownBy(() -> userService.login(account, password))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Values for authorization is missing.");
    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    void register() {
        // given
        String account = "account";
        String password = "password";
        String email = "email";

        // when
        User user = userService.register(account, password, email);

        // then
        assertAll(
                () -> assertThat(user.getAccount()).isEqualTo(account),
                () -> assertThat(user.getPassword()).isEqualTo(password),
                () -> assertThat(user.getEmail()).isEqualTo(email)
        );
    }

    @DisplayName("누락된 정보가 있어서 회원가입에 실패한다.")
    @Test
    void cannotRegister() {
        // given
        String account = "account";
        String password = "password";
        String email = "";

        // when & then
        assertThatThrownBy(() -> userService.register(account, password, email))
                .isInstanceOf(InvalidRegisterException.class)
                .hasMessage("Values for register is missing.");
    }
}
