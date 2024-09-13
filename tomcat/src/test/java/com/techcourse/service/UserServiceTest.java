package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.except.UnauthorizedException;
import com.techcourse.except.UserNotFoundException;
import com.techcourse.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private UserService userService = new UserService();

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.truncate();
    }

    @DisplayName("isAccountExist() 메서드는 계정이 존재하면 true를 반환한다.")
    @Test
    void isAccountExist() {
        // given
        String account = "account";
        User user = new User(account, "password", "email");
        InMemoryUserRepository.save(user);

        // when
        boolean exists = userService.isAccountExist(account);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("isAccountExist() 메서드는 계정이 존재하지 않으면 false를 반환한다.")
    @Test
    void isAccountNotExist() {
        // when
        boolean exists = userService.isAccountExist("nonExistingUser");

        // then
        assertThat(exists).isFalse();
    }

    @DisplayName("checkUser() 메서드는 비밀번호가 맞으면 아무런 예외도 발생하지 않는다.")
    @Test
    void checkUserSuccess() {
        // given
        User user = new User("existingUser", "password", "email@example.com");

        // when && then
        assertThatCode(() -> userService.checkUser(user, "password")).doesNotThrowAnyException();
    }

    @DisplayName("checkUser() 메서드는 비밀번호가 틀리면 UnauthorizedException을 발생시킨다.")
    @Test
    void checkUserFailure() {
        // given
        User user = new User("existingUser", "password", "email@example.com");

        // when && then
        assertThatThrownBy(() -> userService.checkUser(user, "wrongPassword"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("findBy() 메서드는 계정이 존재하면 User를 반환한다.")
    @Test
    void findByExistingUser() {
        // given
        User user = new User("existingUser", "password", "email@example.com");
        InMemoryUserRepository.save(user);

        // when
        User findUser = userService.findBy("existingUser");

        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getAccount()).isEqualTo("existingUser");
    }

    @DisplayName("findBy() 메서드는 계정이 존재하지 않으면 UserNotFoundException을 발생시킨다.")
    @Test
    void findByNonExistingUser() {
        // when && then
        assertThatThrownBy(() -> userService.findBy("nonExistingUser"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }
}


