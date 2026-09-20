package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserTest {

    @Test
    void 회원_정보를_가진다() {
        // given & when
        final var user = new User(1L, "gugu", "password", "gugu@example.com");

        // then
        assertThat(user.getAccount()).isEqualTo("gugu");
        assertThat(user.checkPassword("password")).isTrue();
        assertThat(user).hasToString(
                "User{id=1, account='gugu', email='gugu@example.com', password='password'}"
        );
    }

    @ParameterizedTest
    @CsvSource({
            "password, true",
            "wrong, false"
    })
    void 입력된_비밀번호가_비밀번호와_일치하는지_판단한다(
            String input,
            boolean expected
    ) {
        // given
        final var user = new User("gugu", "password", "gugu@example.com");

        // when
        final boolean actual = user.checkPassword(input);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 계정을_반환한다() {
        // given
        final var user = new User("gugu", "password", "gugu@example.com");

        // when
        final String account = user.getAccount();

        // then
        assertThat(account).isEqualTo("gugu");
    }

    @Test
    void 회원_정보를_문자열로_표현한다() {
        // given
        final var user = new User(1L, "gugu", "password", "gugu@example.com");

        // when
        final String actual = user.toString();

        // then
        assertThat(actual).isEqualTo(
                "User{id=1, account='gugu', email='gugu@example.com', password='password'}"
        );
    }
}
