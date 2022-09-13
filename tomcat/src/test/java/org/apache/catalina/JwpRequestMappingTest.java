package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.controller.handlermapping.JwpRequestMapping;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.StaticFileController;
import nextstep.jwp.exception.ControllerNotFoundException;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.URL;
import org.junit.jupiter.api.Test;

class JwpRequestMappingTest {

    private RequestMapping requestMapping = JwpRequestMapping.getInstance();

    @Test
    void map() {
        // given
        final URL url = URL.of("/login");

        // when
        final Controller actual = requestMapping.map(url);

        // then
        assertThat(actual).isInstanceOf(LoginController.class);
    }

    @Test
    void mapStatic() {
        // given
        final URL url = URL.of("/login.html");

        // when
        final Controller actual = requestMapping.map(url);

        // then
        assertThat(actual).isInstanceOf(StaticFileController.class);
    }

    @Test
    void controllerNotFound() {
        // given
        final URL url = URL.of("/not-found");

        // when
        assertThatThrownBy(() -> requestMapping.map(url))
                .isExactlyInstanceOf(ControllerNotFoundException.class);
    }
}
