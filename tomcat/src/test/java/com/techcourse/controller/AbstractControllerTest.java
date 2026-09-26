package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    void GET_요청이면_doGet을_호출한다() throws Exception {
        final TestController controller = new TestController();
        final HttpRequest request = HttpRequest.from("GET /test HTTP/1.1");
        final HttpResponse response = new HttpResponse(new ByteArrayOutputStream());

        controller.service(request, response);

        assertThat(controller.getCalled).isTrue();
        assertThat(controller.postCalled).isFalse();
    }

    @Test
    void POST_요청이면_doPost를_호출한다() throws Exception {
        final TestController controller = new TestController();
        final HttpRequest request = HttpRequest.from("POST /test HTTP/1.1");
        final HttpResponse response = new HttpResponse(new ByteArrayOutputStream());

        controller.service(request, response);

        assertThat(controller.getCalled).isFalse();
        assertThat(controller.postCalled).isTrue();
    }

    @Test
    void 허용하지_않는_메서드는_405를_응답한다() throws Exception {
        final TestController controller = new TestController();
        final HttpRequest request = HttpRequest.from("DELETE /test HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .isEqualTo(String.join("\r\n",
                        "HTTP/1.1 405 Method Not Allowed",
                        "Allow: GET, POST",
                        "Content-Length: 0",
                        "",
                        ""));
    }

    @Test
    void 재정의하지_않은_메서드는_405를_응답한다() throws Exception {
        final GetOnlyController controller = new GetOnlyController();
        final HttpRequest request = HttpRequest.from("POST /test HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .isEqualTo(String.join("\r\n",
                        "HTTP/1.1 405 Method Not Allowed",
                        "Allow: GET",
                        "Content-Length: 0",
                        "",
                        ""));
    }

    private static class TestController extends AbstractController {

        private boolean getCalled;
        private boolean postCalled;

        private TestController() {
            super("GET", "POST");
        }

        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) {
            getCalled = true;
        }

        @Override
        protected void doPost(final HttpRequest request, final HttpResponse response) {
            postCalled = true;
        }
    }

    private static class GetOnlyController extends AbstractController {

        private GetOnlyController() {
            super("GET");
        }

        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) {
            // NOOP
        }
    }
}
