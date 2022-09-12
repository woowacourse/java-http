package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerMappingTest {

    private final RequestHandlerMapping requestHandlerMapping = new RequestHandlerMapping();

    @Test
    @DisplayName("입력 받은 path에 매핑되는 핸들러가 존재하지 않으면 예외가 발생한다.")
    void exception_noMappingHandler() {
        // when & then
        assertThatThrownBy(() -> requestHandlerMapping.getHandler("/wrong"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessageContaining("Invalid Uri");
    }

    @Test
    @DisplayName("getHandler 메소드는 입력 받은 path에 매핑되는 핸들러를 반환한다.")
    void success() {
        // when
        final RequestHandler handler = requestHandlerMapping.getHandler("/login");

        // then
        assertThat(handler).isInstanceOf(LoginController.class);
    }
}