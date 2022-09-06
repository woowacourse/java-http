package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    @DisplayName("요청을 처리 가능한 컨트롤러를 반환한다.")
    void findHandleController() {
        // given
        RequestMapping requestMapping = new RequestMapping(List.of(new LoginController(), new DefaultController()));

        // when
        Controller controller = requestMapping.getController(HttpRequest.ofRequestLine("GET /login HTTP/1.1"));

        // then
        assertThat(controller.canHandle(HttpRequest.ofRequestLine("GET /login HTTP/1.1"))).isTrue();
    }

    @Test
    @DisplayName("요청을 처리 가능한 컨트롤러가 없다면 디폴트 컨트롤러를 반환한다.")
    void findDefaultController() {
        // given
        RequestMapping requestMapping = new RequestMapping(List.of(new LoginController(), new DefaultController()));

        // when
        Controller controller = requestMapping.getController(HttpRequest.ofRequestLine("GET /not HTTP/1.1"));

        // then
        assertThat(controller instanceof DefaultController).isTrue();
    }
}
