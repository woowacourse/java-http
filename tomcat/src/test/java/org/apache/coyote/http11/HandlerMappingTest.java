package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import nextstep.jwp.LoginController;
import org.apache.coyote.Handler;
import org.apache.coyote.HandlerMappings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMappingTest {

    @Test
    @DisplayName("path를 입력받아 이를 처리할 수 있는 핸들러를 찾아 반환한다.")
    void getAdaptiveHandler() {
        HandlerMappings handlerMappings = new HandlerMappings();
        Handler handler = handlerMappings.getAdaptiveHandler("/login");

        assertThat(handler).isInstanceOf(LoginController.class);
    }

    @Test
    @DisplayName("입력된 path를 처리할 핸들러가 없는 경우 null을 반환한다.")
    void returnNullIfNoHandler() {
        HandlerMappings handlerMappings = new HandlerMappings();
        Handler handler = handlerMappings.getAdaptiveHandler("/no-handler");

        assertThat(handler).isNull();
    }
}
