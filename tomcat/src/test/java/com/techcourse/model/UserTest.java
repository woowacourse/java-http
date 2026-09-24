package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserTest {

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
}
