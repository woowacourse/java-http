package com.techcourse.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterRequestDtoTest {

    @Test
    @DisplayName("null 이거나 빈 문자열의 경우 예외를 발생한다.")
    void validate() {
        assertThatThrownBy(() -> new RegisterRequestDto(null, "test", "test"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RegisterRequestDto("test", null, "test"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RegisterRequestDto("test", "test", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
