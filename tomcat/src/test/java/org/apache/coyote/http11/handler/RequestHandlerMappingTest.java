package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.handler.LoginHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerMappingTest {

    private final RequestHandlerMapping requestHandlerMapping = RequestHandlerMapping.init();

    @Test
    @DisplayName("hasMappingHandler 메소드는 입력 받은 path에 매핑되는 핸들러가 존재하는지 판별한다.")
    void hasMappingHandler() {
        // when
        final boolean response = requestHandlerMapping.hasMappingHandler("/login");

        // then
        assertThat(response).isTrue();
    }

    @Test
    @DisplayName("getHandler 메소드는 입력 받은 path에 매핑되는 핸들러를 반환한다.")
    void getHandler() {
        // when
        final RequestHandler handler = requestHandlerMapping.getHandler("/login");

        // then
        assertThat(handler).isInstanceOf(LoginHandler.class);
    }
}