package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    void getController_success() {
        // given
        final FakeController controller = new FakeController();
        final RequestMapping requestMapping = new RequestMapping();

        requestMapping.addController("/login", controller);

        final HttpRequest request = createRequest(
                "GET /login HTTP/1.1"
        );

        // when
        final Controller result =
                requestMapping.getController(request);

        // then
        assertThat(result).isSameAs(controller);
    }

    @Test
    void getController_success_ifHasQueryString() {
        // given
        final FakeController controller = new FakeController();
        final RequestMapping requestMapping = new RequestMapping();

        requestMapping.addController("/login", controller);

        final HttpRequest request = createRequest(
                "GET /login?redirect=/index.html HTTP/1.1"
        );

        // when
        final Controller result =
                requestMapping.getController(request);

        // then
        assertThat(result).isSameAs(controller);
    }

    private HttpRequest createRequest(final String requestLine) {
        final String httpRequest = String.join(
                "\r\n",
                requestLine,
                "",
                ""
        );

        return new HttpRequest(
                new BufferedReader(
                        new StringReader(httpRequest)
                )
        );
    }
}