package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginRequestHandlerTest {
    @Test
    void 비밀번호가_일치하지_않으면_예외가_발생한다() {
        // given
        final LoginRequestHandler loginRequestHandler = new LoginRequestHandler();

        final Map<String, String> paramsMap = Map.of(
                "account", "gugu",
                "password", "wrong-password"
        );

        // when & then
        assertThatThrownBy(() -> loginRequestHandler.handle(paramsMap))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호 불일치");
    }

}