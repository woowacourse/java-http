package org.apache.coyote.http11;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    @Test
    void 요청_경로에_등록된_컨트롤러를_반환한다() {
        final Controller controller = (request, response) -> { };
        final Map<String, Controller> controllers = new HashMap<>();
        controllers.put("/login", controller);
        final RequestMapping requestMapping = new RequestMapping(controllers);
        controllers.clear();

        assertThat(requestMapping.getController(request("/login")))
                .containsSame(controller);
    }

    @Test
    void 요청_경로에_등록된_컨트롤러가_없으면_빈_값을_반환한다() {
        final RequestMapping requestMapping = new RequestMapping(Map.of());

        assertThat(requestMapping.getController(request("/unknown"))).isEmpty();
    }

    private HttpRequest request(final String path) {
        return HttpRequest.builder()
                .httpMethod(HttpMethod.GET)
                .path(path)
                .version("HTTP/1.1")
                .build();
    }
}
