package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.NotFoundController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerMapperTest {

    @DisplayName("유효하지 않은 경로가 주어졌을 때 NotFoundController를 반환한다.")
    @Test
    void should_returnNotFoundController_when_invalidPath() {
        // given
        String path = "/invalid";

        // when
        Controller controller = ControllerMapper.find(path);

        // then
        assertThat(controller).isInstanceOf(NotFoundController.class);
    }
}
