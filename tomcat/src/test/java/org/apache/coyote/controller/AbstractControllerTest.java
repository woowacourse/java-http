package org.apache.coyote.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    void GET_요청이면_doGet을_호출한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET");
        final HttpResponse response = new HttpResponse();
        final TestController controller = new TestController();

        // when
        controller.service(request, response);

        // then
        assertThat(controller.getCalled).isTrue();
        assertThat(controller.postCalled).isFalse();
    }

    @Test
    void POST_요청이면_doPost를_호출한다() throws Exception {

        // given
        final HttpRequest request = createRequest("POST");

        final HttpResponse response = new HttpResponse();

        final TestController controller = new TestController();

        // when
        controller.service(request, response);

        // then
        assertThat(controller.postCalled).isTrue();

        assertThat(controller.getCalled).isFalse();
    }

    @Test
    void 지원하지_않는_HTTP_Method는_405_응답을_반환한다() throws Exception {

        // given
        final HttpRequest request = createRequest("PUT");

        final HttpResponse response = new HttpResponse();

        final TestController controller = new TestController();

        // when
        controller.service(request, response);

        // then
        assertThat(controller.getCalled).isFalse();

        assertThat(controller.postCalled).isFalse();

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.writeTo(outputStream);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 405 Method Not Allowed")
                .contains("Allow: GET, POST")
                .contains("Content-Length: 0");
    }

    private HttpRequest createRequest(final String method) throws Exception {
        final String rawRequest = String.join(
                "\r\n",
                method
                        + " /test HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        return HttpRequest.from(inputStream).orElseThrow();
    }

    private static class TestController extends AbstractController {

        private boolean getCalled;
        private boolean postCalled;

        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) {
            getCalled = true;
        }

        @Override
        protected void doPost(final HttpRequest request, final HttpResponse response) {
            postCalled = true;
        }
    }
}