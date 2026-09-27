package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    void GET_요청을_doGet에_위임한다() throws Exception {
        final RecordingController controller = new RecordingController();

        controller.service(request(HttpMethod.GET), new HttpResponse());

        assertThat(controller.getInvoked).isTrue();
        assertThat(controller.postInvoked).isFalse();
    }

    @Test
    void POST_요청을_doPost에_위임한다() throws Exception {
        final RecordingController controller = new RecordingController();

        controller.service(request(HttpMethod.POST), new HttpResponse());

        assertThat(controller.postInvoked).isTrue();
        assertThat(controller.getInvoked).isFalse();
    }

    private HttpRequest request(final HttpMethod httpMethod) {
        return HttpRequest.builder()
                .httpMethod(httpMethod)
                .path("/")
                .version("HTTP/1.1")
                .build();
    }

    private static final class RecordingController extends AbstractController {

        private boolean getInvoked;
        private boolean postInvoked;

        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) {
            getInvoked = true;
        }

        @Override
        protected void doPost(final HttpRequest request, final HttpResponse response) {
            postInvoked = true;
        }
    }
}
