package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerMapperTest {

    @Test
    @DisplayName("해당 uri를 처리할 수 있는 컨트롤러를 반환한다.")
    void findController() {
        Optional<Controller> controller = ControllerMapper.findController("/login");

        assertThat(controller.isPresent()).isTrue();
        assertDoesNotThrow(() -> (LoginController)controller.get());
    }

    @Test
    @DisplayName("처리할 수 있는 컨트롤러가 없는 경우 빈 값을 반환한다.")
    void invalidUri() {
        Optional<Controller> controller = ControllerMapper.findController("/abcdefg");

        assertThat(controller.isEmpty()).isTrue();
    }

}
