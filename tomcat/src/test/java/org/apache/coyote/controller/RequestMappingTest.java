package org.apache.coyote.controller;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    private final Controller loginController = new TestController();

    private final Controller registerController = new TestController();

    private final RequestMapping requestMapping = new RequestMapping(
            Map.of(
                    "/login",
                    loginController,
                    "/register",
                    registerController
            )
    );

    private static class TestController implements Controller {
        @Override
        public void service(final HttpRequest request, final HttpResponse response) {
        }
    }

    @Test
    void 등록된_경로의_Controller를_반환한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /login HTTP/1.1");

        // when
        final Controller controller = requestMapping.getController(request).orElseThrow();

        // then
        assertThat(controller).isSameAs(loginController);
    }

    @Test
    void 등록되지_않은_경로면_Controller가_없다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /index.html HTTP/1.1");

        // when & then
        assertThat(requestMapping.getController(request)).isEmpty();
    }

    private HttpRequest createRequest(final String requestLine) throws Exception {

        final String rawRequest = String.join(
                "\r\n",
                requestLine,
                "Host: localhost:8080",
                "",
                ""
        );

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        return HttpRequest.from(inputStream).orElseThrow();
    }
}