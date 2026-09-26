package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void 계정과_비밀번호가_일치하면_유저를_반환한다() {
        assertThat(userService.login("gugu", "password")).isPresent();
    }

    @Test
    void 비밀번호가_틀리면_빈_값을_반환한다() {
        assertThat(userService.login("gugu", "wrong")).isEmpty();
    }

    @Test
    void 회원가입한_유저로_로그인할_수_있다() {
        userService.register("newbie", "pw", "newbie@woowahan.com");

        assertThat(userService.login("newbie", "pw")).isPresent();
    }
}
