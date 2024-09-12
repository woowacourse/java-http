package com.techcourse.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("유저 테스트")
class UserTest {

    @DisplayName("유저의 비밀번호가 특정 비밀번호와 일치하는 지 확인할 수 있다")
    @Test
    void checkPassword() {
        // given
        String correctPassword = "password";
        String incorrectPassword = "incorrectPassword";
        User user = new User("account", correctPassword, "email@test.com");
        // when & then
        Assertions.assertAll(
                () -> assertThat(user.checkPassword(correctPassword)).isTrue(),
                () -> assertThat(user.checkPassword(incorrectPassword)).isFalse()
        );
    }
}
