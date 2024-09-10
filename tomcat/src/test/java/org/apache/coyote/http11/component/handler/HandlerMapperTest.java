package org.apache.coyote.http11.component.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMapperTest {

    @Test
    @DisplayName("URI 정보를 바탕으로 handler를 매핑해준다.")
    void map_handler_via_uri_path() {
        // given
        final var loginHandler = new LoginHandler("/login");
        HandlerMapper.add(loginHandler.getPath(), loginHandler);

        // when
        final var httpHandler = HandlerMapper.get(loginHandler.getPath());

        // then
        assertThat(httpHandler).isInstanceOf(LoginHandler.class);
    }

}
