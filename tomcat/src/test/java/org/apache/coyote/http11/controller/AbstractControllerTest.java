package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.coyote.http11.request.HttpRequest;
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

    private HttpRequest createRequest(final String requestLine) {
        final String httpRequest = String.join(
                "\r\n",
                requestLine,
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(httpRequest)
        );

        return new HttpRequest(reader);
    }
}