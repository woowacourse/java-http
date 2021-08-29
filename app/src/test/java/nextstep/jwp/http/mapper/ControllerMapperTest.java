package nextstep.jwp.http.mapper;

import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.http.controller.RedirectController;
import nextstep.jwp.http.controller.stationary.CssController;
import nextstep.jwp.http.controller.stationary.HtmlController;
import nextstep.jwp.http.controller.stationary.JavaScriptController;
import nextstep.jwp.http.exception.HttpUriNotFoundException;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ControllerMapperTest {

    @DisplayName("적절한 컨트롤러를 찾는다. (RedirectController)")
    @Test
    void matchRedirectController() {
        // given
        HttpRequestMessage redirectMessage = makeGetRequestMessage("redirect:/");
        ControllerMapper controllerMapper = new ControllerMapper();

        // when
        Controller controller = controllerMapper.matchController(redirectMessage);

        // then
        assertThat(controller).isInstanceOf(RedirectController.class);
    }

    @DisplayName("적절한 컨트롤러를 찾는다. (HtmlController)")
    @Test
    void matchHtmlController() {
        // given
        HttpRequestMessage redirectMessage = makeGetRequestMessage("/index.html");
        ControllerMapper controllerMapper = new ControllerMapper();

        // when
        Controller controller = controllerMapper.matchController(redirectMessage);

        // then
        assertThat(controller).isInstanceOf(HtmlController.class);
    }

    @DisplayName("적절한 컨트롤러를 찾는다. (CssController)")
    @Test
    void matchCssController() {
        // given
        HttpRequestMessage redirectMessage = makeGetRequestMessage("/css/style.css");
        ControllerMapper controllerMapper = new ControllerMapper();

        // when
        Controller controller = controllerMapper.matchController(redirectMessage);

        // then
        assertThat(controller).isInstanceOf(CssController.class);
    }

    @DisplayName("적절한 컨트롤러를 찾는다. (JavaScriptController)")
    @Test
    void matchJavaScriptController() {
        // given
        HttpRequestMessage redirectMessage = makeGetRequestMessage("/js/script.js");
        ControllerMapper controllerMapper = new ControllerMapper();

        // when
        Controller controller = controllerMapper.matchController(redirectMessage);

        // then
        assertThat(controller).isInstanceOf(JavaScriptController.class);
    }

    @Test
    @DisplayName("해당 요청에 매핑 정보가 없으면 예외가 발생한다.")
    void matchControllerFromNonMapping() {
        // given
        HttpRequestMessage redirectMessage = makeGetRequestMessage("/nonmappingqasdzxc");
        ControllerMapper controllerMapper = new ControllerMapper();

        // when, then
        assertThatThrownBy(() -> controllerMapper.matchController(redirectMessage))
                .isInstanceOf(HttpUriNotFoundException.class);
    }

    @DisplayName("매핑 정보가 있는 컨트롤러를 찾는다.")
    @Test
    void matchController() {
        // given
        HttpRequestMessage redirectMessage = makeGetRequestMessage("/");
        ControllerMapper controllerMapper = new ControllerMapper();

        // when
        Controller controller = controllerMapper.matchController(redirectMessage);

        // then
        assertThat(controller).isInstanceOf(HelloController.class);
    }

    private HttpRequestMessage makeGetRequestMessage(String requestUri) {
        String requestMessage = String.join("\r\n",
                String.format("GET %s HTTP/1.1 ", requestUri),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        return new HttpRequestMessage(requestMessage);
    }
}