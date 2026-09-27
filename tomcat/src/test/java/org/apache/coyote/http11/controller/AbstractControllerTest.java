package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestInput;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private final FakeController controller = new FakeController();

    @Test
    void getRequest_callDoGet_success() throws Exception {
        // given
        final HttpRequest request = createRequest(
                "GET /login HTTP/1.1"
        );
        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(controller.isGetCalled()).isTrue();
        assertThat(controller.isPostCalled()).isFalse();
    }

    @Test
    void postRequest_callDoPost_success() throws Exception {
        // given
        final HttpRequest request = createRequest(
                "POST /login HTTP/1.1"
        );
        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(controller.isPostCalled()).isTrue();
        assertThat(controller.isGetCalled()).isFalse();
    }

    private HttpRequest createRequest(final String httpRequest) {
        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(
                        httpRequest.getBytes(StandardCharsets.UTF_8)
                );

        return new HttpRequest(
                new HttpRequestInput(inputStream)
        );
    }
}