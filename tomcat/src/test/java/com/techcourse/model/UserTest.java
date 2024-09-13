package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("비밀번호가 일치하는지 확인한다.")
    @Test
    void checkPasswordTest() {
        // given
        User user = new User("mangcho", "1234", "mangcho@woowa.com");

        // when, then
        assertThat(user.checkPassword("1234")).isTrue();
    }

    @DisplayName("계정을 가져온다.")
    @Test
    void getAccountTest() {
        // given
        User user = new User("mangcho", "1234", "mangcho@woowa.com");

        // when, then
        assertThat(user.getAccount()).isEqualTo("mangcho");
    }
}
