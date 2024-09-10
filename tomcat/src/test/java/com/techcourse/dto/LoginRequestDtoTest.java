package com.techcourse.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginRequestDtoTest {

    @Test
    @DisplayName("null 이거나 빈 문자열의 경우 예외를 발생한다.")
    void validate() {
        assertThatThrownBy(() -> new LoginRequestDto(null, "test"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new LoginRequestDto("test", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new LoginRequestDto("", "test"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new LoginRequestDto("test", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
