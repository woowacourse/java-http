package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

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
