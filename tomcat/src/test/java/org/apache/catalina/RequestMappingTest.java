package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.StaticFileController;
import org.apache.catalina.exception.ControllerNotFoundException;
import org.apache.coyote.Controller;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private RequestMapping requestMapping = new RequestMapping();

    @Test
    void map() {
        // given
        final String url = "/login";

        // when
        final Controller actual = requestMapping.map(url);

        // then
        assertThat(actual).isInstanceOf(LoginController.class);
    }

    @Test
    void mapStatic() {
        // given
        final String url = "/login.html";

        // when
        final Controller actual = requestMapping.map(url);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }

    @Test
    void controllerNotFound() {
        // given
        final String url = "/not-found";

        // when
        assertThatThrownBy(() -> requestMapping.map(url))
                .isExactlyInstanceOf(ControllerNotFoundException.class);
    }
}
