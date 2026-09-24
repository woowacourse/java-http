package org.qupring.mvc.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpTomcatRequest;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.Test;
import org.qupring.mvc.controller.Controller;

class RequestMappingTest {

    @Test
    void 요청_URI에_등록된_컨트롤러를_반환한다() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        Controller controller = (request, response) -> {
        };
        requestMapping.addController("/test", controller);

        // when
        Controller mappedController = requestMapping.getController(request("/test"));

        // then
        assertThat(mappedController).isSameAs(controller);
    }

    @Test
    void 등록되지_않은_URI는_null을_반환한다() {
        // given
        RequestMapping requestMapping = new RequestMapping();

        // when
        Controller controller = requestMapping.getController(request("/unknown"));

        // then
        assertThat(controller).isNull();
    }

    private HttpRequest request(String path) {
        return new HttpTomcatRequest(
                HttpMethod.GET,
                path,
                "HTTP/1.1",
                null,
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of()
        );
    }
}
