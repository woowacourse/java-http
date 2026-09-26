package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    void 요청_경로에_등록된_Controller를_반환한다() throws Exception {
        Controller loginController = new TestController();
        Controller defaultController = new TestController();

        RequestMapping mapping = new RequestMapping(defaultController);
        mapping.register("/login", loginController);

        HttpRequest request = request("GET /login HTTP/1.1");

        assertThat(mapping.getController(request))
                .isSameAs(loginController);
    }

    @Test
    void 등록되지_않은_요청에는_기본_Controller를_반환한다()
            throws Exception {
        Controller defaultController = new TestController();

        RequestMapping mapping = new RequestMapping(defaultController);

        HttpRequest request = request("GET /unknown HTTP/1.1");

        assertThat(mapping.getController(request))
                .isSameAs(defaultController);
    }

    private HttpRequest request(final String requestLine)
            throws Exception {
        String rawRequest = String.join("\r\n",
                requestLine,
                "",
                ""
        );

        return HttpRequest.readFrom(
                new ByteArrayInputStream(
                        rawRequest.getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    private static class TestController
            extends AbstractController {
    }
}
