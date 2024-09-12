package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @DisplayName("URI에 해당하는 Controller를 반환한다.")
    @Test
    void getControllerTest() {
        // given
        String uri = "/login";

        // when
        Controller controller = RequestMapping.getController(uri);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @DisplayName("URI에 해당하는 Controller가 없을 때 StaticResourceController를 반환한다.")
    @Test
    void getControllerTest1() {
        // given
        String uri = "/notfound";

        // when
        Controller controller = RequestMapping.getController(uri);

        // then
        assertThat(controller).isInstanceOf(StaticResourceController.class);
    }
}
