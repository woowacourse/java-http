package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.handler.LoginServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestServletMappingTest {

    private final RequestServletMapping requestServletMapping = RequestServletMapping.init();

    @Test
    @DisplayName("입력 받은 path에 매핑되는 핸들러가 존재하지 않으면 예외가 발생한다.")
    void exception_noMappingHandler() {
        // when & then
        assertThatThrownBy(() -> requestServletMapping.getHandler("/wrong"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Uri");
    }

    @Test
    @DisplayName("getHandler 메소드는 입력 받은 path에 매핑되는 핸들러를 반환한다.")
    void success() {
        // when
        final RequestServlet handler = requestServletMapping.getHandler("/login");

        // then
        assertThat(handler).isInstanceOf(LoginServlet.class);
    }
}