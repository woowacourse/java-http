package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestInput;
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
        final Optional<Controller> result =
                requestMapping.getController(request);

        // then
        assertThat(result).contains(controller);
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
        final Optional<Controller> result =
                requestMapping.getController(request);

        // then
        assertThat(result).contains(controller);
    }

    private HttpRequest createRequest(
            final String httpRequest
    ) {
        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(
                        httpRequest.getBytes(StandardCharsets.UTF_8)
                );

        return new HttpRequest(
                new HttpRequestInput(inputStream)
        );
    }
}
