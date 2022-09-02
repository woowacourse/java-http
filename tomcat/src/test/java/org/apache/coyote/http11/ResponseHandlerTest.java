package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseHandlerTest {

    @DisplayName("존재하지 않는 유저일 경우 예외가 발생한다.")
    @Test
    void existUserException() {
        final ResponseHandler handler = new ResponseHandler("/login?account=gu&password=password");

        assertThatThrownBy(
                handler::getResponse)
                .hasMessageContaining("존재하지 않는 유저입니다.")
                .isInstanceOf(UserNotFoundException.class);
    }
}
