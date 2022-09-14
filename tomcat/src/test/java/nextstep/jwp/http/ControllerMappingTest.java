package nextstep.jwp.http;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.servlet.ControllerMapping;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.support.RequestInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ControllerMappingTest {

    private final ControllerMapping controllerMapping = new ControllerMapping();

    @Test
    void uri에_해당하는_컨트롤러가_존재하면_해당_컨트롤러를_반환한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(HttpMethod.GET, "/login");
        // when
        final Controller controller = controllerMapping.getController(requestInfo);
        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void uri에_해당하는_컨틀롤러가_존재하지_않고_ResourceSuffix의_형식을_따르면_ResourceController를_반환한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(HttpMethod.GET, "/index.html");
        // when
        final Controller controller = controllerMapping.getController(requestInfo);
        // then
        assertThat(controller).isInstanceOf(ResourceController.class);
    }

    @Test
    void uri에_해당하는_컨트롤러가_존재하지_않고_ResourceSuffix의_형식을_따르지_않으면_예외를_반환한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(HttpMethod.GET, "/index");
        // when, given
        assertThatThrownBy(() -> controllerMapping.getController(requestInfo))
                .isInstanceOf(CustomNotFoundException.class);
    }
}
