package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.AuthenticationException;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void 계정과_비밀번호가_일치하면_유저를_반환한다() {
        assertThat(userService.login("gugu", "password").getAccount()).isEqualTo("gugu");
    }

    @Test
    void 비밀번호가_틀리면_예외가_발생한다() {
        assertThatThrownBy(() -> userService.login("gugu", "wrong"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 회원가입한_유저로_로그인할_수_있다() {
        userService.register("newbie", "pw", "newbie@woowahan.com");

        assertThat(userService.login("newbie", "pw").getAccount()).isEqualTo("newbie");
    }
}
