package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import nextstep.jwp.LoginController;
import org.apache.coyote.Controller;
import org.apache.coyote.ControllerMappings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerMappingsTest {

    @Test
    @DisplayName("path를 입력받아 이를 처리할 수 있는 핸들러를 찾아 반환한다.")
    void getAdaptiveHandler() {
        ControllerMappings controllerMappings = new ControllerMappings();
        Controller controller = controllerMappings.getAdaptiveController("/login");

        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    @DisplayName("입력된 path를 처리할 핸들러가 없는 경우 null을 반환한다.")
    void returnNullIfNoHandler() {
        ControllerMappings controllerMappings = new ControllerMappings();
        Controller controller = controllerMappings.getAdaptiveController("/no-handler");

        assertThat(controller).isNull();
    }
}
