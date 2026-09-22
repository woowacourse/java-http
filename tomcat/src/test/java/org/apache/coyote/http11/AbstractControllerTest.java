package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    void delegatesGetRequestToDoGet() throws Exception {
        final var controller = new TestController();

        final var response = controller.service(request("GET"));

        assertThat(response.headerBytes())
                .isEqualTo("HTTP/1.1 200 OK\r\n\r\n".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void delegatesPostRequestToDoPost() throws Exception {
        final var controller = new TestController();

        final var response = controller.service(request("POST"));

        assertThat(response.headerBytes())
                .isEqualTo("HTTP/1.1 302 Found\r\n\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private HttpRequest request(final String method) {
        return new HttpRequest(method, "/", "HTTP/1.1", Map.of());
    }

    private static final class TestController extends AbstractController {
        @Override
        protected HttpResponse doGet(final HttpRequest request) {
            return HttpResponse.of("200 OK", new byte[0]);
        }

        @Override
        protected HttpResponse doPost(final HttpRequest request) {
            return HttpResponse.of("302 Found", new byte[0]);
        }
    }
}
