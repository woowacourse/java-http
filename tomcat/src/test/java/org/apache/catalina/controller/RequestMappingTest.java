package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    private final Controller loginController = (request, response) -> { };
    private final RequestMapping requestMapping = new RequestMapping(Map.of("/login", loginController));

    @Test
    void 등록된_경로면_해당_컨트롤러를_찾는다() {
        final Controller controller = requestMapping.getController(request("GET /login HTTP/1.1"));

        assertThat(controller).isSameAs(loginController);
    }

    @Test
    void 쿼리_스트링이_있어도_경로로_찾는다() {
        final Controller controller = requestMapping.getController(request("GET /login?next=/ HTTP/1.1"));

        assertThat(controller).isSameAs(loginController);
    }

    @Test
    void 등록되지_않은_경로면_정적_리소스_컨트롤러를_찾는다() {
        final Controller controller = requestMapping.getController(request("GET /index.html HTTP/1.1"));

        assertThat(controller).isInstanceOf(StaticResourceController.class);
    }

    private HttpRequest request(final String requestLine) {
        return HttpRequest.of(RequestLine.from(requestLine), RequestHeaders.from(List.of()), "");
    }
}
