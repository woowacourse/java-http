package com.techcourse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("사용자 정보")
class UserTest {

    @Test
    @DisplayName("사용자 정보를 문자열로 표현할 때 비밀번호는 노출하지 않는다")
    void toStringDoesNotExposePassword() {
        // given
        final var user = new User(1L, "gugu", "super-secret", "gugu@example.com");

        // when
        final var actual = user.toString();

        // then
        assertThat(actual).doesNotContain("super-secret");
    }
}
